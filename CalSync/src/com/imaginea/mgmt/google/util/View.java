package com.imaginea.mgmt.google.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.google.api.client.util.ClassInfo;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class View {
	private static final Logger logger = Logger.getLogger(View.class);
	static void header(String name) {
		logger.info("============== " + name + " ==============");
	}

	static void display(CalendarList feed) {
		if (feed.getItems() != null) {
			for (CalendarListEntry entry : feed.getItems()) {
				logger.info("-----------------------------------------------");
				display(entry);
			}
		}
	}

	static void display(Events feed) {
		if (feed.getItems() != null) {
			logger.info("Total Events : " + feed.getItems().size());
			for (Event entry : feed.getItems()) {
				logger.info("-----------------------------------------------");
				display(entry);
			}
		}else{
			logger.info("No events found");    	
		}
	}

	static void display(CalendarListEntry entry) {
		logger.info("ID: " + entry.getId());
		logger.info("Summary: " + entry.getSummary());
		logger.info("Description: " + entry.getDescription());
		logger.info("AccessRole: " + entry.getAccessRole());
		logger.info("Kind: " + entry.getKind());
		logger.info("Location: " + entry.getLocation());
		logger.info("ClassInfo: " + entry.getClassInfo());
		logger.info("Primary: " + entry.getPrimary());
		logger.info("Etag: " + entry.getEtag());
		ClassInfo ci = entry.getClassInfo();
		logger.info("ClassInfo: " + ci.getNames() );
	}

	static void display(Calendar entry) {
		logger.info("ID: " + entry.getId());
		logger.info("Summary: " + entry.getSummary());
		if (entry.getDescription() != null) {
			logger.info("Description: " + entry.getDescription());
		}
	}

	static void display(Event event) {  	  
		logger.info("-----------------------------------------------");
		logger.info("ID: " +event.getId()); 
		logger.info("Description: " +event.getDescription()); 
		logger.info("Summary: " +event.getSummary()); 
		logger.info("CalUID: " +event.getICalUID()); 
		logger.info("Location: " +event.getLocation()); 
		logger.info("Status: " +event.getStatus()); 
		logger.info("Attendees: " +event.getAttendees()); 
		logger.info("Creator: " +event.getCreator()); 
		logger.info("Created On: " +event.getCreated()); 
		logger.info("Organizer: " +event.getOrganizer()); 
		logger.info("Recurrence: " +event.getRecurrence()); 
		logger.info("Start Time: " + event.getStart());
		logger.info("End Time: " + event.getEnd());
	}
	static void display(String msg) {
		logger.info(msg);
	}

	static void display(Exception e) {
		logger.error(e);
//        String parsedString = null;
//		try {
//	        StringWriter writer = new StringWriter();
//	        e.printStackTrace(new PrintWriter(writer));
//	        parsedString = writer.getBuffer().toString();
//        }catch (Exception ex) {
//		}
//		logger.info(parsedString);
	}	

}
