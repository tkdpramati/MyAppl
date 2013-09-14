package com.imaginea.mgmt.google.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.imaginea.mgmt.google.util.CalendarSyncUtil;

public class MainService implements Job{
	private static final Logger logger = Logger.getLogger(MainService.class);
	private static CalendarSyncUtil utility = null;
	public MainService(){
		//System.out.println("Service started ...");
		if(utility == null) utility = new CalendarSyncUtil();		
	}
	@Override
	public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
	    try {
			utility.execute();
			//System.out.println(utility.getAccounts());
		} catch (Exception e) {
			logger.error(e);
		}finally{
			if(utility != null) utility.reset();
		}
	}

}
