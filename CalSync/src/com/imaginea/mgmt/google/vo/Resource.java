package com.imaginea.mgmt.google.vo;

import com.google.api.services.calendar.model.Events;
public class Resource {
	private String id;
	private String name;
	private String description;
	private String type;
	private String calendarId;
	private Events events;
	private String emailEncodedPart;
	public Resource(String id, String name, String description, String type, String emailEncodedPart) {
		this.id = id.replace(" ", "_").replace("'", "");
		this.name = name; //.replace(" ", "_").replace("'", "");
		this.description = description;
		this.type = type;
		this.emailEncodedPart = emailEncodedPart;
	}
	
	public Events getEvents() {
		return events;
	}

	public void setEvents(Events events) {
		this.events = events;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getType() {
		return type;
	}	
	public String getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(String domain) {
		this.calendarId = domain+ "_" + this.emailEncodedPart + "@resource.calendar.google.com";
	}

	@Override
	public String toString() {
		return "Resource [id=" + id + ", name=" + name 	+ ", description=" + description 
				+ ", type=" + type + ", calendarId=" + calendarId + "]";
	}
//	@Override
//	public String toString() {
//		return "<resource id=\"" + id + "\" name=\"" + name + "\" description=\"" + description 
//				+ "\" type=\"" + type + "\" email=\"" + email + "\" emailEncodedPart=\"" + emailEncodedPart + "\"/>";
//	}
}
