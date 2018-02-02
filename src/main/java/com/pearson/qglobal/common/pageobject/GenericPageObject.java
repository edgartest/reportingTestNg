package com.pearson.qglobal.common.pageobject;

import org.openqa.selenium.WebDriver;

public class GenericPageObject extends QGPageObject{
	
    protected String pageUrl = "";
    
    public GenericPageObject(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Making sure that we don't register a generic page with the Page Factory.
     */
    @Override
    public void registerPageObject() {
    }
    
    @Override
    public String getPageUrl() {
        return pageUrl;
    }
    
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
    
    public void navigateToPageObjectUrl() {
        driver.get(pageUrl);
    }
}
