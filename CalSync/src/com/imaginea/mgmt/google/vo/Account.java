package com.imaginea.mgmt.google.vo;

import java.util.List;
import java.util.Set;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.imaginea.mgmt.google.util.LoadDetails;

public class Account {
	private String name;
	private String domain;
	private String jsonFile;
	private String storeFile;
	private String keyFile;
	private String appName;
	private String apiKey;
	private String accountId;
	private String accountUser;
	private Set<String> toBeUpdated;
	private Calendar calService;
	//private Map<String, Resource> resources = null;
	private List<Resource> resources = null;
//	private void setResources(){
//		resources = new ArrayList<Resource>();
//		resources = new HashMap<String, Resource>();
//		resources.put("O'Hare", new Resource("O'Hare-Hyd-2nd Floor-Meeting-12-LCD-WB","O'Hare","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500035","Meeting","4f486172652d4879642d326e645f466c6f6f722d4d656574696e672d31322d4c43442d5742"));
//		resources.put("Narita", new Resource("Narita-Hyd-2nd Floor-Meeting-4-WB","Narita","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500036","Meeting","4e61726974612d4879642d326e645f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Broadway", new Resource("Broadway-Hyd-2nd Floor-Meeting-4-WB","Broadway","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500034","Meeting","42726f61647761792d4879642d326e645f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Shinjuka", new Resource("Shinjuka-Hyd-2nd Floor-Meeting-4-WB","Shinjuka","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500035","Meeting","5368696e6a756b612d4879642d326e645f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Schipol", new Resource("Schipol-Hyd-2nd Floor-Meeting-7-WB","Schipol","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500036","Meeting","53636869706f6c2d4879642d326e645f466c6f6f722d4d656574696e672d372d5742"));
//		resources.put("Kings Cross", new Resource("Kings Cross-Hyd-3rd Floor-Meeting-4-WB","Kings Cross","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500037","Meeting","4b696e67735f43726f73732d4879642d3372645f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Shibuya", new Resource("Shibuya-Hyd-3rd Floor-Meeting-4-WB","Shibuya","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500038","Meeting", "536869627579612d4879642d3372645f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Grand Central", new Resource("Grand Central-Hyd-3rd Floor-Meeting-4-WB","Grand Central","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500039","Meeting", "4772616e645f43656e7472616c2d4879642d3372645f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Union Station", new Resource("Union Station-Hyd-3rd Floor-Meeting-6-WB","Union Station","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500040","Meeting","556e696f6e5f53746174696f6e2d4879642d3372645f466c6f6f722d4d656574696e672d362d5742"));
//		resources.put("Gare du Nord", new Resource("Gare du Nord-Hyd-3rd Floor-Meeting-12-WB","Gare du Nord","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500041","Meeting","476172655f64755f4e6f72642d4879642d3372645f466c6f6f722d4d656574696e672d31322d5742"));
//		resources.put("Piazza Navona", new Resource("Piazza Navona-Hyd-4th Floor-Meeting-4-WB","Piazza Navona","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500042","Meeting","5069617a7a615f4e61766f6e612d4879642d3474685f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Piccadilly Circus", new Resource("Piccadilly Circus-Hyd-4th Floor-Meeting-4-WB","Piccadilly Circus","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500043","Meeting","506963636164696c6c795f4369726375732d4879642d3474685f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Trafalgar Square", new Resource("Trafalgar Square-Hyd-4th Floor-Meeting-4-WB","Trafalgar Square","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500044","Meeting","54726166616c6761725f5371756172652d4879642d3474685f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Connaught Place", new Resource("Connaught Place-Hyd-4th Floor-Meeting-6-WB","Connaught Place","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500045","Meeting","436f6e6e61756768745f506c6163652d4879642d3474685f466c6f6f722d4d656574696e672d362d5742"));
//		resources.put("Times Square", new Resource("Times Square-Hyd-4th Floor-Meeting-12-WB","Times Square","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500046","Meeting","54696d65735f5371756172652d4879642d3474685f466c6f6f722d4d656574696e672d31322d5742"));
//		resources.put("Wall Street", new Resource("Wall Street-Hyd-5th Floor-Meeting-4-WB","Wall Street","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500047","Meeting", "57616c6c5f5374726565742d4879642d3574685f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Silicon Valley", new Resource("Silicon Valley-Hyd-5th Floor-Meeting-4-WB","Silicon Valley","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500048","Meeting","53696c69636f6e5f56616c6c65792d4879642d3574685f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Canary Wharf", new Resource("Canary Wharf-Hyd-5th Floor-Meeting-12-WB","Canary Wharf","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500049","Meeting","43616e6172795f57686172662d4879642d3574685f466c6f6f722d4d656574696e672d31322d5742"));
//		resources.put("Bond Street", new Resource("Bond Street-Hyd-5th Floor-Meeting-4-WB","Bond Street","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500050","Meeting","426f6e645f5374726565742d4879642d3574685f466c6f6f722d4d656574696e672d342d5742"));
//		resources.put("Nariman Point", new Resource("Nariman Point-Hyd-5th Floor-Meeting-6-WB","Nariman Point","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500051","Meeting","4e6172696d616e5f506f696e742d4879642d3574685f466c6f6f722d4d656574696e672d362d5742"));
//		resources.put("Ghirardelli Square", new Resource("Ghirardelli Square-Hyd-5th Floor-Meeting-8-WB","Ghirardelli Square","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500052","Meeting","47686972617264656c6c695f5371756172652d4879642d3574685f466c6f6f722d4d656574696e672d382d5742"));
//		resources.put("Charing Cross", new Resource("Charing Cross-Hyd-5th Floor-Training-16-WB","Charing Cross","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500053","Training","43686172696e675f43726f73732d4879642d3574685f466c6f6f722d547261696e696e672d31362d5742"));
//		resources.put("Lexington Avenue", new Resource("Lexington Avenue-Hyd-4th Floor-Training-16-WB","Lexington Avenue","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500054","Training","4c6578696e67746f6e5f4176656e75652d4879642d3474685f466c6f6f722d547261696e696e672d31362d5742"));
//		resources.put("Penn Station", new Resource("Penn Station-Hyd-3rd Floor-Training-20-WB","Penn Station","MidTown 6-3-348, Road No. 1, Banjara Hills, Hyderabad, AP, India, 500055","Training","50656e6e5f53746174696f6e2d4879642d3372645f466c6f6f722d547261696e696e672d32302d5742"));		
//	}
	
	public void reset(){
		if(resources != null){
			for(Resource resource : resources){
				Events events =  resource.getEvents();
				if(events != null){
					List<Event> items = events.getItems();
					if(items != null){
						int n = events.getItems().size();
						for (int i=0; i <n; i++) {
							items.remove(i);
						}
			    	}
					events.clear();
				}
		    	resource.setEvents(null);
			}
		}
	}	
	
	public List<Resource> getResources() {
		return resources;
	}

	
	public Account(){}
	public Account(String name, String appName, String apiKey, String accountId, String accountUser) {
		super();
		this.name = name;
		this.domain = name + ".com";
		this.jsonFile = name +".client_secrets.json";
		this.storeFile = ".credentials/" + name +".calendar.json";
		this.keyFile = ".credentials/" + name +".privatekey.p12";
		this.appName = appName;
		this.apiKey = apiKey;
		this.accountId = accountId;
		this.accountUser = accountUser;
		this.resources = LoadDetails.getResources();
		for(Resource resource : this.resources)	resource.setCalendarId(this.domain);
	}	
	
	public Calendar getCalService() {
		return calService;
	}

	public void setCalService(Calendar calService) {
		this.calService = calService;
	}
		
	public Set<String> getToBeUpdated() {
		return toBeUpdated;
	}


	public void setToBeUpdated(Set<String> toBeUpdated) {
		this.toBeUpdated = toBeUpdated;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}
	public String getJsonFile() {
		return jsonFile;
	}
	public String getStoreFile() {
		return storeFile;
	}
	public String getKeyFile() {
		return keyFile;
	}
	public String getAppName() {
		return appName;
	}
	public String getApiKey() {
		return apiKey;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getAccountUser() {
		return accountUser;
	}

	@Override
	public String toString() {
		return "Account [name=" + name + ", domain=" + domain + ", appName=" + appName 
			    + ", accountId=" + accountId + ", accountUser=" + accountUser
				+ ", toBeUpdated=" + toBeUpdated + "]";
	}	

//	@Override
//	public String toString() {
//		return "<account name=\"" + name + "\" applName=\"" + appName + "\" apiKey=\"" + apiKey
//				+ "\" accountId=\"" + accountId + "\" accountUser=\"" + accountUser	+ "\">";
//	}	

}
