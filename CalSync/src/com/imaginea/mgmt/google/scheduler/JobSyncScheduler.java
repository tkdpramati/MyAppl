package com.imaginea.mgmt.google.scheduler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.imaginea.mgmt.google.service.MainService;

public class JobSyncScheduler {
	private static final Logger logger = Logger.getLogger(JobSyncScheduler.class);
	public static void main(String[] args) {
		try {
			int frequency = 120;
			if(args.length > 0) {
				String text = args[0];
				boolean isMin = false;
				if(args.length == 2){
					isMin = "-fm".equals(text);
					text += args[1];
				}				
				Pattern  objPattern =  Pattern.compile("(\\d+)");
				Matcher objMatcher = objPattern.matcher(text);
				if(objMatcher.find()){
					text = objMatcher.group();
					frequency = Integer.parseInt(text);
					if(isMin) frequency = 60 * frequency;
				}
			}
			logger.info("Scheduler started with frequency : " + frequency + " secs");
			JobDetail job = JobBuilder.newJob(MainService.class).withIdentity("SyncService").build();
			Trigger trigger = TriggerBuilder.newTrigger().
					withSchedule(  
	                    SimpleScheduleBuilder.simpleSchedule()
	                    .withIntervalInSeconds(frequency)
	                    .repeatForever()).build();  
			//schedule the job
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.start();	    	
			scheduler.scheduleJob(job, trigger);	
			
		} catch (SchedulerException e) {
    		logger.error(e);
		}
	}
}
