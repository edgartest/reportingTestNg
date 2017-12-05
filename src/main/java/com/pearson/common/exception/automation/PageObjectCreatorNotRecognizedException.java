package com.pearson.common.exception.automation;

import org.openqa.selenium.WebDriver;

import com.pearson.common.exception.AutomationException;

public class PageObjectCreatorNotRecognizedException extends AutomationException {

    private static final long serialVersionUID = 1L;

    public PageObjectCreatorNotRecognizedException() {
        super("Unable to instantiate class instance");
    }
    
    public PageObjectCreatorNotRecognizedException(String message) {
        super("Unable to instantiate class instance: " + message);
    }
    
    public PageObjectCreatorNotRecognizedException(String message, Throwable cause) {
        super("Unable to instantiate class instance: " + message, cause);
    }
    
    public PageObjectCreatorNotRecognizedException(WebDriver driver) {
        super("Unable to instantiate class instance: ", driver);
    }
    
    public PageObjectCreatorNotRecognizedException(String message, WebDriver driver) {
        super("Unable to instantiate class instance: " + message, driver);
    }
    
    public PageObjectCreatorNotRecognizedException(String message, Throwable cause, WebDriver driver) {
        super("Unable to instantiate class instance: " + message, cause, driver);
    }
}