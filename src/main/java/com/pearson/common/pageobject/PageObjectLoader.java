package com.pearson.common.pageobject;

import java.util.List;

import org.openqa.selenium.WebDriver;

public interface PageObjectLoader {
	
	public void setupDefaults(WebDriver driver) throws Exception;
	
	public void setup(List<?> list);
	
	public void registerAllClasses(WebDriver driver) throws Exception;
	
	public void registerAllClasses(Class<? extends PageObject> pageObjectClass, WebDriver driver) throws Exception;
	
}
