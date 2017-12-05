package com.pearson.common.exception.automation;

import org.openqa.selenium.WebDriver;

import com.pearson.common.exception.AutomationException;

public class WebElementNotChangedAttributeException extends AutomationException {

    private static final long serialVersionUID = 1L;

    public WebElementNotChangedAttributeException() {
        super("Web element attribute not expected");
    }
    
    public WebElementNotChangedAttributeException(String message) {
        super("Web element attribute not expected: " + message);
    }
    
    public WebElementNotChangedAttributeException(String message, Throwable cause) {
        super("Web element attribute not expected: " + message, cause);
    }
    
    public WebElementNotChangedAttributeException(WebDriver driver) {
        super("Web element attribute not expected: ", driver);
    }
    
    public WebElementNotChangedAttributeException(String message, WebDriver driver) {
        super("Web element attribute not expected: " + message, driver);
    }
    
    public WebElementNotChangedAttributeException(String message, Throwable cause, WebDriver driver) {
        super("Web element attribute not expected: " + message, cause, driver);
    }
}