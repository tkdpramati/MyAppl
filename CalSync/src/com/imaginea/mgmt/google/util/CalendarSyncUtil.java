package com.imaginea.mgmt.google.util;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.imaginea.mgmt.google.factory.CalendarFactory;
import com.imaginea.mgmt.google.vo.Account;
import com.imaginea.mgmt.google.vo.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class CalendarSyncUtil {
	private static final Logger logger = Logger.getLogger(View.class);
	private Map<String, Account> accounts =  null;	
	private com.google.api.services.calendar.Calendar client = null;
	private Set<String> creators = new HashSet<String>();
	public CalendarSyncUtil(String name){
	  	try {
			this.accounts = LoadDetails.getAccounts();			
			this.client = CalendarFactory.getCalendar(name);
		} catch (Exception e) {
			View.display(e);
		}
	}
	
	private void setCalendarService(){
		Set<Entry<String, Account>> accSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSet){		
			Account account = accEntry.getValue();
			creators.add(account.getAccountUser());
			//client = account.getCalService();
			try {
				account.setCalService(CalendarFactory.getCalendar(account));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public CalendarSyncUtil(){
		try {
			this.accounts = LoadDetails.getAccounts();	
			this.setCalendarService();
		} catch (Exception e) {
			View.display(e);
		}
	}

	public Map<String, Account> getAccounts() {
		return accounts;
	}

	public void reset(){
		Set<Entry<String, Account>> accSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSet){
			Account account = accEntry.getValue();
			//account.setCalService(null);	
			account.reset();
		}
	}
	
	public void execute() throws Exception{
		Properties properties = LoadDetails.getProperties();
		Set<Entry<String, Account>> accSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSet){			
			try{
				String accKey = accEntry.getKey();
				Account account = accEntry.getValue();
				logger.info(account.toString());
				//client = CalendarFactory.getCalendar(account);
				client = account.getCalService();
				List<Resource> resources = account.getResources();
				for(Resource resource : resources){
					logger.info(resource.toString());
					resource.setCalendarId(account.getDomain());
					String lastTime = properties.getProperty(accKey + "." + resource.getId() + ".timeMin");
					DateTime  timeMin = DateTime.parseRfc3339(lastTime);
					Events events = getAllEvents(resource.getCalendarId(), timeMin);
					View.display(events);
					resource.setEvents(events);
					properties.setProperty(accKey + "." + resource.getId() + ".timeMin", new DateTime(new Date(),TimeZone.getDefault()).toStringRfc3339());
				}
				//account.setCalService(client);
				Set<String> set = accounts.keySet();
				Set<String> tempSet = new HashSet<String>();
				for(String val : set){
					tempSet.add(val);
				}
				tempSet.remove(accKey);
				logger.info("############################################["+set + "---" + tempSet+"]##################################################");
				account.setToBeUpdated(tempSet);
			}catch (Exception e) {
				View.display(e);
			}
		}		
		try{
			syncEvents();
			//deleteEvents();
		}catch (Exception e) {
			View.display(e);
		}
		LoadDetails.saveProperties(properties);
	}
	
	private void syncEvents() throws Exception{
		View.header( "Sync all Calendars at " + new Date());
		Set<Entry<String, Account>> accSyncSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSyncSet){
			Account srcAccount = accEntry.getValue();
			Set<String> tarAccSet = srcAccount.getToBeUpdated();
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + accEntry.getKey() + " --- " + tarAccSet + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			if(tarAccSet != null){
				for(String tarAccKey : tarAccSet){
					Account tarAccount = accounts.get(tarAccKey);			
					client = tarAccount.getCalService();
					if(client != null){
						List<Resource> resources = srcAccount.getResources();
						for(Resource resource : resources){
							resource.setCalendarId(tarAccount.getDomain());
							Events events = resource.getEvents();
							if(events != null && events.getItems() != null && !events.getItems().isEmpty()){
								View.header( "[ " + srcAccount.getDomain() + " -> " + tarAccount.getDomain() + " for " + resource.getId() + "]");
								//addEventsUsingBatch(client, resource, srcAccount.getAccountUser());
								addAllEvents(client, resource, srcAccount.getAccountUser());
							}
						}
					}
				}
			}
		}			
	}
	
	private void addEvent(com.google.api.services.calendar.Calendar client, String calendar, Event event) throws IOException {
		View.header("################################Add Event######################################");
		Event result = client.events().insert(calendar, event).execute();
		View.display(result);
	}
	
	private void addAllEvents(com.google.api.services.calendar.Calendar tgtclient, Resource resource, String accUser) throws IOException {
		String calId = resource.getCalendarId(); 
		Events events = resource.getEvents();
	    if (events.getItems() != null) {
	    	View.header(resource.getId());
	    	for (Event event : events.getItems()) {
	    		View.display(event);
	    		Creator creator = event.getCreator();
	    		if(creator != null &&  !creators.contains(creator.getEmail())) {
		    		logger.info("Account user :"+ accUser + " event creator :" + creator.getEmail());
	    			logger.info("Event added: " +  calId);
		    		Event clonedEvt = eventClone(event);
		    		View.display(clonedEvt);
		    		try{
		    			Event result = tgtclient.events().insert(calId, clonedEvt).execute();
		    			View.display(result);
		    			//addEvent(tgtclient, calId, clonedEvt);
		    		}catch(Exception e){
						View.display(e);
		    		}
	    		}else{
	    			logger.info("Event not added.....");
	    		}
	        }
	    }	    
	}
	private void addEventsUsingBatch(com.google.api.services.calendar.Calendar tgtclient, Resource resource, String accUser) throws IOException {
		final java.util.List<Event> allEvents = Lists.newArrayList();
		BatchRequest batch = tgtclient.batch();
		// Create the callback.
		JsonBatchCallback<Event> callback = new JsonBatchCallback<Event>() {
			@Override
			public void onSuccess(Event event, HttpHeaders responseHeaders) {
				View.display("On success: " + responseHeaders.toString());
				View.display(event);
				allEvents.add(event);					
			}

			@Override
			public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
				View.display("Error Message: " + e.getMessage());
			}
		};
		
		String calId = resource.getCalendarId(); 
		Events events = resource.getEvents();
	    if (events.getItems() != null) {
	    	View.header(resource.getId());
	    	for (Event event : events.getItems()) {
	    		View.display(event);
	    		Creator creator = event.getCreator();
	    		if(creator != null &&  !creators.contains(creator.getEmail())) {
		    		logger.info("Account user :"+ accUser + " event creator :" + creator.getEmail());
	    			logger.info("Event added: " +  calId);
		    		Event clonedEvt = eventClone(event);
		    		View.display(clonedEvt);
		    		try{
		    		tgtclient.events().insert(calId, clonedEvt).queue(batch, callback);
		    		}catch(Exception e){
		    			e.printStackTrace();
		    		}
	    		}else{
	    			logger.info("Event not added.....");
	    		}
	        }
	    }	    
	    if(!allEvents.isEmpty()){
	    	batch.execute();	
	    }else{
			logger.info("No events added.....");	    	
	    }
	}
	private void deleteEvents() throws Exception{
		View.header( "Delete all evets from all Calendars at " + new Date());
		Set<Entry<String, Account>> accSyncSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSyncSet){
			Account account = accEntry.getValue();
			client = account.getCalService();
			if(client != null){
				List<Resource> resources = account.getResources();
				try{
					for(Resource resource : resources){
						Events events = resource.getEvents();
						if(events != null && events.getItems() != null && !events.getItems().isEmpty()){
							View.header( "[Delete from " + account.getDomain() + " for " + resource.getId() + "]");
							deleteEventsUsingBatch(client, resource, account.getAccountUser());
						}
					}
				}catch(Exception e){
					View.display(e);
				}
			}
		}	
	}

	private void deleteEventsUsingBatch(com.google.api.services.calendar.Calendar tgtclient, Resource resource, String accountUser) throws IOException {
		BatchRequest batch = tgtclient.batch();
		// Create the callback.
		JsonBatchCallback<Void> callback = new JsonBatchCallback<Void>() {
			@Override
			public void onSuccess(Void content, HttpHeaders responseHeaders) {
				//View.display(content);
				 logger.info("Delete is successful!");
				//allEvents.add(event);					
			}

			@Override
			public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
				View.display("Error Message: " + e.getMessage());
			}
		};
		
		String calId = resource.getCalendarId(); 
		Events events = resource.getEvents();
	    if (events.getItems() != null) {
	    	View.header(resource.getId());
	    	for (Event event : events.getItems()) {
	    		View.display(event);
	    		logger.info("Created by :"+ accountUser);
	    		Creator creator = event.getCreator();
	    		if(creator != null &&  accountUser.equals(creator.getEmail())){
	    			logger.info("Detete it :"+ creator.getEmail());
	    			tgtclient.events().delete(calId, event.getId()).queue(batch, callback);
	    		}
	        }
	    }
	    try{
	    	batch.execute();
	    }catch (Exception e) {
			View.display(e);
		}
		//client = null;		
	}

	private Event eventClone(Event event) {
		 Event evt = new Event();
		 evt.setDescription(event.getDescription());
		 evt.setLocation(event.getLocation());
		 evt.setStatus(event.getStatus());
		 evt.setSummary(event.getSummary());
		 //evt.setAttendees(event.getAttendees());
		 //evt.setCreator(event.getCreator());
		 //evt.setOrganizer(event.getOrganizer());
		 //evt.setRecurrence(event.getRecurrence());
		 evt.setStart(event.getStart());
		 evt.setEnd(event.getEnd());
		 //evt.setAttendeesOmitted(event.getAttendeesOmitted());
		 //evt.setAnyoneCanAddSelf(event.getAnyoneCanAddSelf());
		 //evt.setCreated(event.getCreated());
		 //evt.setEndTimeUnspecified(event.getEndTimeUnspecified());
		 //evt.setEtag(event.getEtag());
		 //evt.setExtendedProperties(event.getExtendedProperties());
		 //evt.setFactory(event.getFactory());
		 //evt.setGuestsCanInviteOthers(event.getGuestsCanInviteOthers());
		 //evt.setGuestsCanModify(event.getGuestsCanModify());
		 //evt.setGuestsCanSeeOtherGuests(event.getGuestsCanSeeOtherGuests());
		 //evt.setHangoutLink(event.getHangoutLink());
		 //evt.setHtmlLink(event.getHtmlLink());
		 //evt.setKind(event.getKind());
		 //evt.setLocked(event.getLocked());
		 //evt.setOriginalStartTime(event.getOriginalStartTime());
		 //evt.setPrivateCopy(event.getPrivateCopy());
		 //evt.setRecurringEventId(event.getRecurringEventId());
		 //evt.setReminders(event.getReminders());
		 //evt.setSequence(event.getSequence());
		 //evt.setTransparency(event.getTransparency());
		 //evt.setUnknownKeys(event.getUnknownKeys());
		 //evt.setUpdated(event.getUpdated());
		 //evt.setVisibility(event.getVisibility());
		 //evt.setColorId(event.getColorId());
		 return evt;
	}

	
	private Events getAllEvents(String calendarId, DateTime timeMin) throws IOException{
		Events feed = client.events().list(calendarId).setUpdatedMin(timeMin).execute();
		return feed;
	}
	
	 public void updateEvent(String calendarId) throws IOException{
	     Calendar calendar = new Calendar();
	     calendar.setId(calendarId);
	     updateEvent(calendar);
	 }
 
	 public void addEvent(String calendarId) throws IOException{
	     Calendar calendar = new Calendar();
	     calendar.setId(calendarId);
	     Event event = newEvent();
	     event.setDescription("Filter:Test desc from apps portal");
	     event.setLocation("Trafalgar Square");
	     event.setSummary("Filter:Test summary from apps portal");
	     addEvent(calendar,event);
	 }

	 public void deleteEvent(String calendarId) throws IOException {
	     Calendar calendar = new Calendar();
	     calendar.setId(calendarId);
	     deleteEvent(calendar);	 
	 } 
	 
	 public void showEvents(String calendarId) throws IOException {
		 Calendar calendar = new Calendar();
		 calendar.setId(calendarId);
		 showEvents(calendar);
	 }

	 private void addEvent(Calendar calendar) throws IOException {
		 View.header("Add Event");
		 Event event = newEvent();
		 Event result = client.events().insert(calendar.getId(), event).execute();
		 View.display(result);
	 }

	 private void addEvent(Calendar calendar, Event event) throws IOException {
		 View.header("Add Event");
		 Event result = client.events().insert(calendar.getId(), event).execute();
		 View.display(result);
	 }
 
	 private void updateEvent(Calendar calendar) throws IOException {
		 View.header("Update Event");
		 Events feed = client.events().list(calendar.getId()).execute();
		 Event event = null;
		 if (feed.getItems() != null) {
			 for (Event entry : feed.getItems()) {
				 if("ojp9hiclr4ag9ogt1dtn4e3k0k".equals(entry.getId())){
					 event = entry;
					 break;
				 }
			 }
	     }
		 event.setSummary("Pramati : " + event.getSummary());
		 event.setDescription("Pramati : " + event.getDescription());
		 EventAttendee attendee = new EventAttendee();
		 attendee.setEmail("tapan.d@imaginea.com");
		 attendee.setDisplayName("Tapan Kumar");
		 event.setAttendees(Arrays.asList(attendee));
		 event.setLocation("OHare");
		 Event result = client.events().update(calendar.getId(), event.getId(), event).execute();
		 View.display(result);
	 }

	 private void deleteEvent(Calendar calendar) throws IOException {
		 View.header("Delete Event");
		 Events feed = client.events().list(calendar.getId()).execute();
		 Event event = null;
		 if (feed.getItems() != null) {
			 for (Event entry : feed.getItems()) {
				 if("netadmin@imaginea.com".equals(entry.getCreator().getEmail())){
					 event = entry;
					 client.events().delete(calendar.getId(), event.getId()).execute();
				 }
			 }
	     }
	 }
	 
	 private Event newEvent() {
		 Event event = new Event();
		 event.setSummary("New Event");
		 Date startDate = new Date();
		 Date endDate = new Date(startDate.getTime() + 3600000);
		 DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
		 event.setStart(new EventDateTime().setDateTime(start));
		 DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
		 event.setEnd(new EventDateTime().setDateTime(end));
		 return event;
	 }

	 private void showEvents(Calendar calendar) throws IOException {
		 View.header("Show Events");
		 Events feed = client.events().list(calendar.getId()).execute();
		 View.display(feed);
	 }

	 CalendarSyncUtil(boolean s){
			Properties prop =  new Properties();
			try {
				final String XML_FILE_NAME = "google.xml";
				prop.loadFromXML(CalendarSyncUtil.class.getClassLoader().getResourceAsStream(LoadDetails.RESOURCE_PATH +XML_FILE_NAME));
				//System.out.println(prop.getProperty("imaginea.timeMin"));
				prop.setProperty("imaginea.timeMin", new Date().toString());
				prop.setProperty("pramati.timeMin", new Date().toString());
				//System.out.println(CalendarSyncUtil.class.getClassLoader().getResource(LoadDetails.RESOURCE_PATH+XML_FILE_NAME).getFile());
				prop.storeToXML(new FileOutputStream(CalendarSyncUtil.class.getClassLoader().getResource(LoadDetails.RESOURCE_PATH +XML_FILE_NAME).getFile()),"Date Time set at " + new Date());
			} catch (InvalidPropertiesFormatException e) {
				View.display(e);
			} catch (IOException e) {
				View.display(e);
			}
		 
	 }
	 private static String calenderId = "imaginea.com_5dducap8sap04m5s5rt4je1eu4@group.calendar.google.com";

	public static void main(String atgs[]) throws IOException{
		//CalendarSyncUtil ca = new CalendarSyncUtil("tapan");
		//ca.addEvent(calenderId);
		//ca.showEvents(calenderId);
		//ca.deleteEvent(calenderId);
	}
}
