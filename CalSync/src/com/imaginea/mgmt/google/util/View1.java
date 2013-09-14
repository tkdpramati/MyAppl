package com.imaginea.mgmt.google.util;

import org.apache.log4j.Logger;

import com.google.api.client.util.ClassInfo;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class View1 {
	private static final Logger logger = Logger.getLogger(View1.class);
	static void header(String name) {
		System.out.println();
		System.out.println("============== " + name + " ==============");
		System.out.println();
	}

	static void display(CalendarList feed) {
		if (feed.getItems() != null) {
			for (CalendarListEntry entry : feed.getItems()) {
				System.out.println();
				System.out.println("-----------------------------------------------");
				display(entry);
			}
		}
	}

	static void display(Events feed) {
		if (feed.getItems() != null) {
			System.out.println("Total Events : " + feed.getItems().size());
			for (Event entry : feed.getItems()) {
				System.out.println("-----------------------------------------------");
				display(entry);
			}
		}else{
			System.out.println("No events found");    	
		}
	}

	static void display(CalendarListEntry entry) {
		System.out.println("ID: " + entry.getId());
		System.out.println("Summary: " + entry.getSummary());
		System.out.println("Description: " + entry.getDescription());
		System.out.println("AccessRole: " + entry.getAccessRole());
		System.out.println("Kind: " + entry.getKind());
		System.out.println("Location: " + entry.getLocation());
		System.out.println("ClassInfo: " + entry.getClassInfo());
		System.out.println("Primary: " + entry.getPrimary());
		System.out.println("Etag: " + entry.getEtag());
		ClassInfo ci = entry.getClassInfo();
		System.out.println("ClassInfo: " + ci.getNames() );
	}

	static void display(Calendar entry) {
		System.out.println("ID: " + entry.getId());
		System.out.println("Summary: " + entry.getSummary());
		if (entry.getDescription() != null) {
			System.out.println("Description: " + entry.getDescription());
		}
	}

	static void display(Event event) {  	  
		System.out.println("-----------------------------------------------");
		System.out.println("ID: " +event.getId()); 
		System.out.println("Description: " +event.getDescription()); 
		System.out.println("Summary: " +event.getSummary()); 
		System.out.println("CalUID: " +event.getICalUID()); 
		System.out.println("Location: " +event.getLocation()); 
		System.out.println("Status: " +event.getStatus()); 
		System.out.println("Attendees: " +event.getAttendees()); 
		System.out.println("Creator: " +event.getCreator()); 
		System.out.println("Organizer: " +event.getOrganizer()); 
		System.out.println("Recurrence: " +event.getRecurrence()); 
		System.out.println("Start Time: " + event.getStart());
		System.out.println("End Time: " + event.getEnd());
	}
	public static void main(String args[]){
		logger.info("Test logger");
	}
}
