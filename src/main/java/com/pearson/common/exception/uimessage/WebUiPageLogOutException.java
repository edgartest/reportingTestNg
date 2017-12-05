package com.pearson.common.exception.uimessage;

import org.openqa.selenium.WebDriver;

public class WebUiPageLogOutException extends WebUiErrorException {
    
    private static final long serialVersionUID = 1L;

    public WebUiPageLogOutException() {
        super("Page Logout");
    }
    
    public WebUiPageLogOutException(String message) {
        super("Page Logout: " + message);
    }
    
    public WebUiPageLogOutException(String message, Throwable cause) {
        super("Page Logouts: " + message, cause);
    }
    
    public WebUiPageLogOutException(WebDriver driver) {
        super("Page Logout: ", driver);
    }
    
    public WebUiPageLogOutException(String message, WebDriver driver) {
        super("Page Logout: " + message, driver);
    }
    
    public WebUiPageLogOutException(String message, Throwable cause, WebDriver driver) {
        super("Page Logout: " + message, cause, driver);
    }
}
