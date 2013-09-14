package com.imaginea.mgmt.google.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.imaginea.mgmt.google.util.CalendarSyncUtil;
import com.imaginea.mgmt.google.util.LoadDetails;
import com.imaginea.mgmt.google.util.View;
import com.imaginea.mgmt.google.vo.Account;
public class CalendarFactory {
	private static final Logger logger = Logger.getLogger(CalendarFactory.class);

	public static com.google.api.services.calendar.Calendar getCalendar(Account account) throws Exception{
		if(account != null){
			CalendarFactory cf = new CalendarFactory();
			ServiceCalendar cs =  cf.new ServiceCalendar(account);
			return cs.getClientCalendar(false);
		}else{
			return null;
		}
	}
	
	public static com.google.api.services.calendar.Calendar getCalendar(String name) throws Exception{
		Account account = new CalendarSyncUtil().getAccounts().get(name);
		if(account != null){
			CalendarFactory cf = new CalendarFactory();
			ServiceCalendar cs =  cf.new ServiceCalendar(account);
			return cs.getClientCalendar(true);
		}else{
			return null;
		}
	}
	
	class ServiceCalendar {
		  private HttpTransport httpTransport;
		  private JsonFactory jsonFactory = null;
		  private Account account = null;;
		  private ServiceCalendar(Account account){
			  this.account = account;
			  this.jsonFactory = new JacksonFactory();
		  }
		  
		  private Credential authorizeUsingClientSecret() throws Exception {
			  //System.out.println("CalendarFactory#"+CalendarFactory.class.getClassLoader().getResource("").getFile() + LoadDetails.RESOURCE_PATH + account.getJsonFile());
			  //GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileInputStream(System.getProperty("user.dir") + LoadDetails.RESOURCE_PATH +account.getJsonFile()));
			  GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileInputStream(CalendarFactory.class.getClassLoader().getResource("").getFile() + LoadDetails.RESOURCE_PATH + account.getJsonFile()));
		      if (clientSecrets.getDetails().getClientId().startsWith("Enter")
		    		  || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
		    	  throw new Exception("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
		    			  + "into resources/client_secrets.json");
		      }
		      FileCredentialStore credentialStore = new FileCredentialStore(new File(System.getProperty("user.home"), account.getStoreFile()), jsonFactory);
		      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		    		  httpTransport, jsonFactory, clientSecrets,
		            Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(credentialStore).build();
		      return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		  }
		  
		  private Credential authorizeUsingPrivateKey() throws Exception {
			  	GoogleCredential credential = null;
			    try {
			    	try {
			    		//System.out.println("XXXXXXXXXXX"+(new File(System.getProperty("user.home"), account.getKeyFile()).getAbsolutePath()));
			    		credential = new GoogleCredential.Builder().setTransport(httpTransport)
			    				.setJsonFactory(jsonFactory)
			    				.setServiceAccountId(account.getAccountId())
			    				.setServiceAccountScopes(CalendarScopes.CALENDAR)
			    				.setServiceAccountPrivateKeyFromP12File(new File(System.getProperty("user.home"), account.getKeyFile()))
			    				//.setServiceAccountUser(account.getAccountUser())
			    				.build();
			    		
			    	} catch (Exception e) {
			    		logger.error(e);
			    	}
			    } catch (Throwable t) {
		    		logger.error(t);
			    }
			    return credential;
		  }
		  
		  private com.google.api.services.calendar.Calendar getClientCalendar(boolean isPK) {
			  com.google.api.services.calendar.Calendar client = null;
			  try {
				  httpTransport = GoogleNetHttpTransport.newTrustedTransport();
				  Credential credential = isPK ? authorizeUsingPrivateKey() : authorizeUsingClientSecret();
				  client = new com.google.api.services.calendar.Calendar.Builder(
						  httpTransport, jsonFactory, credential).setApplicationName(account.getAppName()).build();
		      }catch(Exception e){
		    		logger.error(e);
		      }
			  return client;
		  }
	}
}
