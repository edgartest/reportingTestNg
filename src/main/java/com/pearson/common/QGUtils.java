package com.pearson.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.pearson.common.exception.uimessage.WebUiBlankPageException;
import com.pearson.common.exception.uimessage.WebUiErrorPageMsgException;
import com.pearson.common.exception.uimessage.WebUiPageErrorException;
import com.pearson.common.exception.uimessage.WebUiPageLogOutException;
import com.pearson.common.exception.uimessage.WebUiPageNotExistsException;
import com.pearson.common.pageobject.PageObject;

public class QGUtils extends Utils{

	public static void analyzeAndThrowException(WebDriver driver, Throwable throwable) throws Exception{
        RuntimeException dirtyException = new RuntimeException(throwable);
        throw dirtyException;
	}

	public static List<Exception> checkWebUiErrors(WebDriver driver, Exception exception, PageObject pageObject) {
		
		if(driver == null) {
            Log.getDefaultLogger().severe("Web Driver is not set or initialized");
            return new LinkedList<Exception>();
        }
        
        String url = pageObject != null ? pageObject.getPageUrl() : driver.getCurrentUrl();
        List<Exception> exceptions = new ArrayList<Exception>();
        
        String currentHeader = pageObject.getHeadingExpressionSaved();
        
        try {
            List<WebElement> elements = null;
            elements = driver.findElements(By.xpath(
                    "//h2[contains(@class,'errorMsg')]"));
            
            if(!elements.isEmpty() && Utils.hasDisplayedElement(driver, elements)) {
                
                String msg = Utils.extractText(elements);
                
                Log.log(driver).warning("Error Message detected");
                
                if(exception != null) {
                    exceptions.add(new WebUiErrorPageMsgException(url + ": " + msg, exception, driver));
                } else {
                    exceptions.add(new WebUiErrorPageMsgException(url + ": " + msg, driver));
                }
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
             
        try {           
            if(currentHeader.contains("Service Unavaiable")) {
            	String msg = "Service Unavialable Detected";
                Log.log(driver).warning(msg);
                
                if(exception != null) {
                    exceptions.add(new WebUiPageErrorException(url + ": " + msg, exception, driver));
                } else {
                    exceptions.add(new WebUiPageErrorException(url + ": " + msg, driver));
                }
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            String nonExistingPage = "The page you are looking for no longer exists.";
            if(nonExistingPage.equals(driver.getTitle())) { 
            	
                Log.log(driver).warning("Detected non existing page");
                String msg = "";
                try{
                	msg = driver.findElement(By.xpath("//*[@id='rightDiv']/div/div/span")).getText();
                }
                catch(Exception e){
                	//stay empty
                }
                
                if(exception != null) {
                    exceptions.add(new WebUiPageNotExistsException(url + ": " + msg, exception, driver));
                } else {
                    exceptions.add(new WebUiPageNotExistsException(url + ": " + msg, driver));
                }
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            List<WebElement> elements = null;
            elements = driver.findElements(By.xpath(".//*[@id='login:signInButton']"));
            
            if(!elements.isEmpty()) {
                
                String msg = "Test Logged Out!";
                
                Log.log(driver).warning(msg);
                
                if(exception != null) {
                    exceptions.add(new WebUiPageLogOutException(url + ": " + msg, exception, driver));
                } else {
                    exceptions.add(new WebUiPageLogOutException(url + ": " + msg, driver));
                }
            }
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        try {
            if(Utils.checkForBlankPage(driver, false)) {
                if(exception != null) {
                    exceptions.add(new WebUiBlankPageException(driver, exception));
                } else {
                    exceptions.add(new WebUiBlankPageException(driver));
                }
            }
            
        } catch (Exception ex) {
            Utils.handleExceptionWarning(driver, ex, false, false);
        }
        
        return exceptions;
	}
}
