package com.imaginea.mgmt.google.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import com.imaginea.mgmt.google.vo.Account;
import com.imaginea.mgmt.google.vo.Resource;

public class LoadDetails {
	public static final String RESOURCE_PATH = ""; 
	public static Map<String, Account> getAccounts() {
		Map<String, Account> accounts = new HashMap<String, Account>();
		final String ACCOUNTS_FILE = RESOURCE_PATH + "accounts.xml";
		try {
			NodeList nodeList = getNodeList(ACCOUNTS_FILE, "account");
    	    for (int count = 0; count < nodeList.getLength(); count++) {
    	    	Node nNode = nodeList.item(count);
    	    	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
    	    		//System.out.println("\nNode Name =" + nNode.getNodeName() + " [OPEN]");
    	    		//System.out.println("Node Value =" + nNode.getTextContent());
    	    		Element eElement = (Element) nNode;
    	    		String name = eElement.getAttribute("name");
    	    		accounts.put(name, new Account(name, eElement.getAttribute("applName"),
	    	    		eElement.getAttribute("apiKey"), eElement.getAttribute("accountId"),
	    	    		eElement.getAttribute("accountUser")));
	    	    	//System.out.println("Node Name =" + nNode.getNodeName() + " [CLOSE]");
    	    	}
    	    }
	    } catch (Exception e) {
			View.display(e);
	    }	 
		return accounts;
	}

	public static List<Resource> getResources() {
		final String RESOURCES_FILE = RESOURCE_PATH + "resources.xml";
		List<Resource> resources = new ArrayList<Resource>();
		try {
			NodeList nodeList = getNodeList(RESOURCES_FILE, "resource");
			for (int count = 0; count < nodeList.getLength(); count++) {
    	    	Node nNode = nodeList.item(count);
    	    	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
    	    		//System.out.println("\nNode Name =" + nNode.getNodeName() + " [OPEN]");
    	    		//System.out.println("Node Value =" + nNode.getTextContent());
    	    		Element eElement = (Element) nNode;
    	    		resources.add(new Resource(eElement.getAttribute("id"),
	    	    		eElement.getAttribute("name"), eElement.getAttribute("description"),
	    	    		eElement.getAttribute("type"),eElement.getAttribute("emailEncodedPart")));
    	    		//System.out.println("Node Name =" + nNode.getNodeName() + " [CLOSE]");
    	    	}
    	    }
	    } catch (Exception e) {
			View.display(e);
	    }	 
		return resources;
	}
	
    private static NodeList getNodeList(String fileName, String tagName){
    	NodeList nList = null;
		try {
    		//System.out.println("LoadDetails#"+ LoadDetails.class.getClassLoader().getResource("").getFile()+ fileName);
			File file = new File(LoadDetails.class.getClassLoader().getResource("").getFile()+ fileName);
	    	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    	Document doc = dBuilder.parse(file);
	    	doc.getDocumentElement().normalize();
	    	//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());	
	    	nList = doc.getElementsByTagName(tagName);
	    } catch (Exception e) {
			View.display(e);
	    }	 
		return nList;
    }
    
    public static Properties getProperties(){
    	final String XML_FILE_NAME = RESOURCE_PATH + "google.xml";
    	Properties properties = new Properties();;
    	try {
    		System.out.println("LoadDetails#"+ LoadDetails.class.getClassLoader().getResource("").getFile()+XML_FILE_NAME);
    		properties.loadFromXML(new FileInputStream(LoadDetails.class.getClassLoader().getResource("").getFile()+XML_FILE_NAME));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			View.display(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			View.display(e);
		}
    	return properties;
    }
    
    public static void saveProperties(Properties properties){
    	final String XML_FILE_NAME = RESOURCE_PATH + "google.xml";
    	try {
    		System.out.println("LoadDetails#"+ LoadDetails.class.getClassLoader().getResource("").getFile()+XML_FILE_NAME);
    		properties.storeToXML(new FileOutputStream(LoadDetails.class.getClassLoader().getResource("").getFile()+ XML_FILE_NAME),"Date Time set at " + new Date());
		} catch (InvalidPropertiesFormatException e) {
			View.display(e);
		} catch (IOException e) {
			View.display(e);
		}
    }

} 
