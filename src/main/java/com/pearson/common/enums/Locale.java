package com.pearson.common.enums;

public enum Locale {
	EN_US("en_us", "English", "USA"),
	UNKNOWN("unknown", "unknown", "unknown");
	
	final private static Locale locale = create();
	private String localeName = null;
	private String language = null;
	private String country = null;
	
	private Locale(String localeName, String language, String country){
		this.localeName = localeName;
		this.language = language;
		this.country = country;
	}
	
	final public static String toStr(){
		return locale.toString();
	}
	
	public static String getLocaleName(){
		return locale.localeName;
	}
	
	public static String getLanguage(){
		return locale.language;
	}
	
	public static String getCountry(){
		return locale.country;
	}
	
	public static Locale create(){
		
		String str = System.getProperty("selenium.locale") == null ? "en_us" : 
					 System.getProperty("selenium.locale").toLowerCase().trim();

		if(str.equals("en_us")){
			return EN_US;
		}else
			return UNKNOWN;	
	}
}
