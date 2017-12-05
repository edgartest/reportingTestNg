package com.pearson.common.exception.uimessage;

import org.openqa.selenium.WebDriver;


public class Http500Exception extends WebUiErrorException {
    
    private static final long serialVersionUID = 1L;

    public Http500Exception() {
        super("Http 500 Exception");
    }
    
    public Http500Exception(String message) {
        super("Http 500 Exception: " + message);
    }
    
    public Http500Exception(String message, Throwable cause) {
        super("Http 500 Exception: " + message, cause);
    }
    
    public Http500Exception(WebDriver driver) {
        super("Http 500 Exception: ", driver);
    }
    
    public Http500Exception(String message, WebDriver driver) {
        super("Http 500 Exception: " + message, driver);
    }
    
    public Http500Exception(String message, Throwable cause, WebDriver driver) {
        super("Http 500 Exception: " + message, cause, driver);
    }
}
