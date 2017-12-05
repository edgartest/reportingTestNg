package com.pearson.common.exception.uimessage;

import org.openqa.selenium.WebDriver;

/**
 * In general, there may be a need to distinguish a 404 exception from just
 * any other.
 */
public class WebUiPageNotExistsException extends WebUiErrorException {
    
    private static final long serialVersionUID = 1L;

    public WebUiPageNotExistsException() {
        super("Page Not Exists");
    }
    
    public WebUiPageNotExistsException(String message) {
        super("Page Not Exists: " + message);
    }
    
    public WebUiPageNotExistsException(String message, Throwable cause) {
        super("Page Not Exists: " + message, cause);
    }
    
    public WebUiPageNotExistsException(WebDriver driver) {
        super("Page Not Exists: ", driver);
    }
    
    public WebUiPageNotExistsException(String message, WebDriver driver) {
        super("Page Not Exists: " + message, driver);
    }
    
    public WebUiPageNotExistsException(String message, Throwable cause, WebDriver driver) {
        super("Page Not Exists: " + message, cause, driver);
    }
}
