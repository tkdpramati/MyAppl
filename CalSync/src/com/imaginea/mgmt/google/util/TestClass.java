package com.imaginea.mgmt.google.util;

public class TestClass {
	public static void main(String[] args){
		System.out.println("TestClass#"+ TestClass.class.getClassLoader().getResource("").getFile());
		System.out.println("user.home#"+ System.getProperty("user.home"));
		System.out.println("user.dir#"+ System.getProperty("user.dir"));
	}
}
