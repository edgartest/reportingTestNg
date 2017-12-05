package com.pearson.common.pageobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.pearson.common.enums.CommonConstants;
import com.pearson.common.Log;
import com.pearson.common.QGUtils;
import com.pearson.common.Utils;
import com.pearson.common.XPath;
import com.pearson.common.exception.AutomationException;
import com.pearson.common.exception.automation.PageObjectInvalidCastException;
import com.pearson.common.exception.automation.PageObjectNotInitializedException;
import com.pearson.common.exception.uimessage.WebUiPageNotExistsException;
import com.pearson.common.exception.uimessage.Http500Exception;
import com.pearson.common.exception.uimessage.WebUiErrorPageMsgException;
import com.pearson.common.exception.uimessage.WebUiPageErrorException;
import com.pearson.common.exception.uimessage.WebUiPageLogOutException;
import com.pearson.common.pagecomponent.Page;


public abstract class PageObject implements Page, PageObjectPrototype, PageObjectRegistrable {
    
    final protected WebDriver driver;
    private Date timeLastChecked = null;
    private boolean errors = false;
    private boolean pageLoaded = false;
    private boolean errorPageMsg = false;
    private boolean pageError = false;
    private boolean http404Page = false;
    private boolean http500Page = false;
    private boolean httpErrorPage = false;
    private String headingExpression = null;
    private String pageBuild = null;
    
    public PageObject(WebDriver driver) {
        this.driver = driver;
    }
    
    @Override
    public String toString() {
        return super.toString().
                concat("[").
                concat(this.getPageUrl()).
                concat("]");
    }
    
    public void initialize() throws Exception {
    }
        
    @Override
    public String getPageBuild() throws Exception {
        String result = "";
        try {
            result = driver.findElement(By.xpath("//div[@class='footer_center']/div[3]/p")).getText().trim();
        } catch(Exception ex) {
            Log.log(driver).warning("Unable to extract pageBuild");
        }
        return result;
    }
    
    public void savePageBuild() throws Exception {
        pageBuild = getPageBuild();
        if(pageBuild.isEmpty()) {
            throw new AutomationException(
                    "Failed to save pageBuild value from footer element", driver);
        }
    }
    /*
     * QG uses h2 as main header, using it as first option
     */
    @Override
    public List<WebElement> getHeadingElements() {
        List<WebElement> elements = new ArrayList<WebElement>();
        try {
            elements = XPath.waitForVisibleElements(driver, "//h2", "2");
        } catch(Exception ex) {
            // Move on to the next set of elements
        }
        if(elements.isEmpty()) {
            try {
                elements = XPath.waitForVisibleElements(driver, "//h1", "2");
            } catch(Exception ex) {
                // Move on to the next set of elements
            }
            
            if(elements.isEmpty()) {
                try {
                    elements = XPath.waitForVisibleElements(driver, "//h3", "2");
                } catch(Exception ex) {
                    // Move on to the next set of elements
                }
            }
            
            if(elements.isEmpty()) {
                try {
                    elements = XPath.waitForVisibleElements(driver, "//h4", "2");
                } catch(Exception ex) {
                    // Move on to the next set of elements
                }
            }
        }
        return elements;
    }
    
    public String getHeadingExpressionSaved(){
    	return headingExpression;
    }
    /**
     * Generates an XPath expression to help wait detect when the page changes
     * or loads.
     * 
     * @return
     */
    public String getHeadingExpression() {
        String expression = "";
        List<WebElement> elements = getHeadingElements();
        for(WebElement element : elements) {
            String text = element.getText().trim();
            if(!text.isEmpty()) {
                expression = "//" + element.getTagName();
                expression += "[contains(text(),\"" + text + "\")]";
                break;
            }
        }
        return expression;
    }
    
    /**
     * This helpful method allows a Page Object to detect any visible e.g. h1 
     * tag and to later detect when content on a page changes (page 
     * changes/loads).
     * 
     * @see waitForHeadingChange
     * @throws Exception
     */
    public void saveHeadingExpression() throws Exception {
        headingExpression = getHeadingExpression();
        if(headingExpression.isEmpty()) {
            throw new AutomationException(
                    "Failed to save XPath expression for a Heading element",
                    driver);
        }
    }
    
    /**
     * Requires that a valid Heading element e.g. h2 has been saved.  This will
     * wait for that element to disappear, effectively waits until the page
     * content has changed.
     * 
     * @see saveHeadingExpression
     * @throws Exception
     */
    public void waitForHeadingChange() throws Exception {
        if(headingExpression == null || headingExpression.isEmpty()) {
            throw new AutomationException(
                    "Failed to wait for Heading element, expression is not set",
                    driver);
        }
        waitForElementDissapear(headingExpression);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends PageObject> T newInstance() throws Exception {
        // TODO: We need to check that the constructor parameters match up.
        // Take a look at the base Page Object Registrar class for a close-up.
        Object object = this.getClass().getConstructors()[0].newInstance(driver);
        checkInstanceOf(object.getClass(), this.getClass());
        return (T)object;
    }
    
    protected void checkInstanceOf(Class<?> class1, Class<?> class2) throws PageObjectInvalidCastException {
        if(!class1.isAssignableFrom(class2)) {
            throw new PageObjectInvalidCastException(class1, class2, driver);
        }
    }

    public void go() throws Exception {
        driver.get(getPageUrl());
        verifyPageObject();
    }
    
    /**
     * In general, this will go out and verify that the DOM, driver's URL, and
     * displaying page is 
     * @throws Exception
     */
    public void verifyPageObject() throws Exception {
        waitForDomComplete();
        waitForUrlLoaded();
        saveHeadingExpression();
        checkWebUiErrors();
    }
        
    public void checkWebUiErrors() throws Exception {
        checkWebUiErrors(true);
    }
    
    public void checkWebUiErrors(boolean throwErrors) throws Exception {
        
        pageLoaded = true;
        timeLastChecked = new Date();
        
        List<Exception> exceptions = QGUtils.checkWebUiErrors(driver, null, this);
        
        if(exceptions!= null && !exceptions.isEmpty()) {
            
            errors = true;
            
            for(Exception e : exceptions) {
                if (e instanceof WebUiPageErrorException) {
                    pageError = true;
     
                } else if(e instanceof WebUiErrorPageMsgException) {
                    errorPageMsg = true;
                    
                } else if(e instanceof WebUiPageNotExistsException) {
                    http404Page = true;
                    httpErrorPage = true;
                    
                } else if (e instanceof Http500Exception) {
                    http500Page = true;
                    httpErrorPage = true;
                    
                } else {
                    httpErrorPage = true;
                }
            }
            
            if(throwErrors) {
                throw exceptions.get(0);
            }
        }
    }
    
    public Date getTimeLastChecked() {
        return new Date(timeLastChecked.getTime());
    }
    
    /*
     * Check for errors PRIOR calling this
     */
    public boolean hasErrors() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return errors;
    }
    
    public boolean isPageLoaded() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return pageLoaded;
    }
    
    public boolean isHttp404Page() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return http404Page;
    }
    
    public boolean isHttp500Page() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return http500Page;
    }
    
    public boolean isHttpErrorPage() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return httpErrorPage;
    }
    
    public boolean isPageError() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return pageError;
    }
    
    public boolean isErrorPageMsg() throws PageObjectNotInitializedException {
        if(timeLastChecked == null) {
            throw new PageObjectNotInitializedException(driver);
        }
        return errorPageMsg;
    }
    
    // --------------------------------------
    
    public void goBack() throws Exception {
        driver.navigate().back();
    }
    
    public void waitForDomComplete() throws Exception {
        Utils.waitForDomComplete(driver);
    }
    
    public void waitForDomInteractive() throws Exception {
        Utils.waitForDomInteractive(driver);
    }
    
    public void waitForUrlLoaded() throws Exception {
        Utils.waitForUrlLoaded(driver, this.getPageUrl());
    }
    
    public WebElement waitForElement(String xpath) throws Exception {
        return XPath.waitForVisibleElement(driver, xpath);
    }
    
    public void waitAndClickElement(String xpath) throws Exception {
        WebElement element = XPath.waitForVisibleElement(driver, xpath);
        moveToAndClick(element);
    }
    
    public void moveToAndClick(WebElement element) throws Exception {
        Utils.moveToAndClick(driver, element);
    }
    
    public void sendKeys(WebElement element, CharSequence keysToSend) throws Exception {
        Utils.sendKeys(driver, element, keysToSend);
    }
    
    public void clearAndSendKeys(WebElement element, CharSequence keysToSend) throws Exception {
        Utils.clearAndSendKeys(driver, element, keysToSend);
    }
    
    public void waitForElementDissapear(String xpath) throws Exception {
        XPath.waitForElementDisappear(driver, xpath);
    }
    
    public void handleException(Exception exception) {
        Utils.handleException(driver, exception);
    }
    
    public void handleException(Exception exception, boolean captureSnapshot) {
        Utils.handleException(driver, exception, captureSnapshot, true, false);
    }
}
