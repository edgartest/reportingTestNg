package com.pearson.common.exception.uimessage;

import org.openqa.selenium.WebDriver;

public class WebUiPageErrorException extends WebUiErrorException {
    
    private static final long serialVersionUID = 1L;

    public WebUiPageErrorException() {
        super("Page Error Exception");
    }
    
    public WebUiPageErrorException(String message) {
        super("Page Error Exception: " + message);
    }
    
    public WebUiPageErrorException(String message, Throwable cause) {
        super("Page Error Exception: " + message, cause);
    }
    
    public WebUiPageErrorException(WebDriver driver) {
        super("Page Error Exception: ", driver);
    }
    
    public WebUiPageErrorException(String message, WebDriver driver) {
        super("Page Error Exception: " + message, driver);
    }
    
    public WebUiPageErrorException(String message, Throwable cause, WebDriver driver) {
        super("Page Error Exception: " + message, cause, driver);
    }
}
