package com.pearson.common.enums;

public enum Environment {
	QA1("qa1", "https://qa1.qglobal.pearsonclinical.com/qg/"),
	STAGING("staging", "https://staging-qglobal.pearsonclinical.com/qg/"),
	PROD("prod", "https://qglobal.pearsonclinical.com/qg/"),
	UNKNOWN("unknown", "unknown");
	
	final private static Environment environment = create();
	private String name = null;
	private String url = null;
	
	private Environment(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	final public static String toStr(){
		return environment.toString();
	}
	
	public static boolean isQA1(){
		return environment.equals(QA1);
	}
	
	public static boolean isStaging(){
		return environment.equals(STAGING);
	}
	
	public static boolean isProd(){
		return environment.equals(PROD);
	}
	
	public static String getName(){
		return environment.name;
	}
	
	public static String getBaseUrl(){
		return environment.url;
	}
	
	public static Environment create(){
		
		String str = System.getProperty("selenium.env") == null ? "unknown" : 
					 System.getProperty("selenium.env").toLowerCase().trim();

		if(str.equals("qa1")){
			return QA1;
		}else if(str.equals("staging")){
			return STAGING;
		}else if(str.equals("prod")){
			return PROD;
		}else
			return UNKNOWN;	
	}
}
