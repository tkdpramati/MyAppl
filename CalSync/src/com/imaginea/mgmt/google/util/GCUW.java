package com.imaginea.mgmt.google.util;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.imaginea.mgmt.google.factory.CalendarFactory;
import com.imaginea.mgmt.google.vo.Account;
import com.imaginea.mgmt.google.vo.Resource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

public class GCUW {
	private static final Map<String, Account> accounts =  new HashMap<String, Account>();	
	static{
		accounts.put("imaginea", new Account("imaginea", "Meeting Room Reservation", "AIzaSyCTvSw-Ylrn0LTwHF2wEAx4GPZ8caqWX7E", "841857880762@developer.gserviceaccount.com", "venu@imaginea.com"));
		accounts.put("pramati", new Account("pramati", "Meeting Room Reservation", "AIzaSyCRxLbRgaPEyWel7Y-yxlfevLgHUCK4RMU", "657171185474-52hf8eodfteioh068kh3grn2h6njth4s@developer.gserviceaccount.com", "jitendra.b@pramati.com"));
	}
	
	public static Map<String, Account> getAccounts() {
		return accounts;
	}

	private com.google.api.services.calendar.Calendar client	= null;
	
	public GCUW(String name){
	  	try {
			this.client = CalendarFactory.getCalendar(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static final String XML_FILE_NAME = "google.xml";
	Properties properties = null;	
	public GCUW() throws Exception{
		properties = new Properties();
		try {
			System.out.println(System.getProperty("user.dir"));
			properties.loadFromXML(new FileInputStream(System.getProperty("user.dir") +"/resources/" + XML_FILE_NAME));
		} catch (Exception e) {
			String errorMsg = "Error while loading props from" + XML_FILE_NAME;
			throw new Exception(errorMsg);
		}
	}
	
	public void reset(){
		Set<Entry<String, Account>> accSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSet){
			Account account = accEntry.getValue();
			account.setCalService(null);	
			account.reset();
		}
	}
	
	public void execute() throws Exception{
		Set<Entry<String, Account>> accSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSet){
			String accKey = accEntry.getKey();
			Account account = accEntry.getValue();
			System.out.println(account.toString());
			client = CalendarFactory.getCalendar(account);
			String lastTime = properties.getProperty(accKey+".timeMin");
			DateTime  timeMin = DateTime.parseRfc3339(lastTime);
			List<Resource> resources = account.getResources();
			for(Resource resource : resources){
				resource.setCalendarId(account.getDomain());
				//System.out.println(resEntry.toString());
				Events events = getAllEvents(resource.getCalendarId(), timeMin);
				//View.display(events);
				resource.setEvents(events);
				//resource.setCalendarId(account.getDomain());
			}
			account.setCalService(client);
			Set<String> set = accounts.keySet();
			Set<String> tempSet = new HashSet<String>();
			for(String val : set){
				tempSet.add(val);
			}
			tempSet.remove(accKey);
			System.out.println(set + "---" + tempSet);
			account.setToBeUpdated(tempSet);
			properties.setProperty(accKey+".timeMin", new DateTime(new Date()).toString());
			
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		}		
		
		Set<Entry<String, Account>> accSyncSet = accounts.entrySet();
		for(Entry<String, Account> accEntry : accSyncSet){
			//String accKey = accEntry.getKey();
			Account srcAccount = accEntry.getValue();
			Set<String> tarAccSet = srcAccount.getToBeUpdated();
			for(String tarAccKey : tarAccSet){
				Account tarAccount = accounts.get(tarAccKey);			
				client = tarAccount.getCalService();
				List<Resource> resources = srcAccount.getResources();
				for(Resource resource : resources){
					View.header( "Resource : " +resource.getName() + " Sync Calendars [ " + srcAccount.getDomain() + " -> " + tarAccount.getDomain() + " ]");
					resource.setCalendarId(tarAccount.getDomain());
					Events events = resource.getEvents();
					if(events.getItems() != null){
						//addEventsUsingBatch(client, resource);
					}
				}
			}
		}	
		properties.storeToXML(new FileOutputStream(System.getProperty("user.dir") +"/resources/" + XML_FILE_NAME),"Date Time set at " + new Date());
	}

	private void addEventsUsingBatch(com.google.api.services.calendar.Calendar tgtclient, Resource resource) throws IOException {
		final java.util.List<Event> allEvents = Lists.newArrayList();
		BatchRequest batch = tgtclient.batch();
		// Create the callback.
		JsonBatchCallback<Event> callback = new JsonBatchCallback<Event>() {
			@Override
			public void onSuccess(Event event, HttpHeaders responseHeaders) {
				try {
					View.display(event);
					allEvents.add(event);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
				System.out.println("Error Message: " + e.getMessage());
			}
		};
		
		String calId = resource.getCalendarId(); 
		//System.out.println("calId="+ calId);
		Events events = resource.getEvents();
	    if (events.getItems() != null) {
	    	for (Event event : events.getItems()) {
	    		//View.display(event);
	    		Event clonedEvt = eventClone(event);
	    		tgtclient.events().insert(calId, clonedEvt).queue(batch, callback);
	        }
	    }
	    try{
			//System.out.println("Batch execution##" + allEvents);	    	
	    	batch.execute();	
	    }catch (Exception e) {
	    	e.printStackTrace();
		}
		//client = null;		
	}

	private Event eventClone(Event event) {
		 Event evt = new Event();
		 evt.setDescription(event.getDescription());
		 evt.setLocation(event.getLocation());
		 evt.setStatus(event.getStatus());
		 evt.setSummary(event.getSummary());
		 evt.setAttendees(event.getAttendees());
		 //evt.setCreator(event.getCreator());
		 //evt.setOrganizer(event.getOrganizer());
		 evt.setRecurrence(event.getRecurrence());
		 evt.setStart(event.getStart());
		 evt.setEnd(event.getEnd());
		 evt.setAttendeesOmitted(event.getAttendeesOmitted());
		 evt.setAnyoneCanAddSelf(event.getAnyoneCanAddSelf());
		 evt.setCreated(event.getCreated());
		 evt.setEndTimeUnspecified(event.getEndTimeUnspecified());
		 evt.setEtag(event.getEtag());
		 evt.setExtendedProperties(event.getExtendedProperties());
		 evt.setFactory(event.getFactory());
		 evt.setGuestsCanInviteOthers(event.getGuestsCanInviteOthers());
		 evt.setGuestsCanModify(event.getGuestsCanModify());
		 evt.setGuestsCanSeeOtherGuests(event.getGuestsCanSeeOtherGuests());
		 evt.setHangoutLink(event.getHangoutLink());
		 evt.setHtmlLink(event.getHtmlLink());
		 evt.setKind(event.getKind());
		 evt.setLocked(event.getLocked());
		 evt.setOriginalStartTime(event.getOriginalStartTime());
		 evt.setPrivateCopy(event.getPrivateCopy());
		 evt.setRecurringEventId(event.getRecurringEventId());
		 evt.setReminders(event.getReminders());
		 evt.setSequence(event.getSequence());
		 evt.setTransparency(event.getTransparency());
		 evt.setUnknownKeys(event.getUnknownKeys());
		 evt.setUpdated(event.getUpdated());
		 evt.setVisibility(event.getVisibility());
		 evt.setColorId(event.getColorId());
		 return evt;
	}

	
	private Events getAllEvents(String calendarId, DateTime timeMin) throws IOException{
		System.out.println(timeMin);
		Events feed = client.events().list(calendarId).setTimeMin(timeMin).execute();
		//Events feed = client.events().list(calendarId).execute();
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
	     event.setDescription("Test desc from apps portal");
	     event.setLocation("Trafalgar Square");
	     event.setSummary("Test summary from apps portal");
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
				 if("20qugtr5qcqro6phealbs7c6oo".equals(entry.getId())){
					 event = entry;
					 break;
				 }
			 }
	     }
		 EventAttendee attendee = new EventAttendee();
		 attendee.setEmail("tapank156@gmail.com");
		 attendee.setDisplayName("Tapan Kumar");
		 event.setAttendees(Arrays.asList(attendee));
		 event.setLocation("OHare");
		 //Event result = client.events().update(calendar.getId(), event.getId(), event).execute();
		 client.events().delete(calendar.getId(), event.getId()).execute();
	 }

	 //
	 
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
}
