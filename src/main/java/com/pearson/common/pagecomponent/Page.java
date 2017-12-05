package com.pearson.common.pagecomponent;

import java.util.List;

import org.openqa.selenium.WebElement;

public interface Page {
    
    public String getPageUrl();    
    public String getPageBuild() throws Exception;
    public List<WebElement> getHeadingElements();
}
