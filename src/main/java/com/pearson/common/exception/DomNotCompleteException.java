package com.pearson.common.exception;

import org.openqa.selenium.WebDriver;


public class DomNotCompleteException extends AutomationException {

    private static final long serialVersionUID = 1L;

    public DomNotCompleteException() {
        super("Log Initialization Failure");
    }
    
    public DomNotCompleteException(String message) {
        super("Log Initialization Failure: " + message);
    }
    
    public DomNotCompleteException(String message, Throwable cause) {
        super("Log Initialization Failure: " + message, cause);
    }
    
    public DomNotCompleteException(WebDriver driver) {
        super("Log Initialization Failure: ", driver);
    }
    
    public DomNotCompleteException(String message, WebDriver driver) {
        super("Log Initialization Failure: " + message, driver);
    }
    
    public DomNotCompleteException(String message, Throwable cause, WebDriver driver) {
        super("Log Initialization Failure: " + message, cause, driver);
    }
}
