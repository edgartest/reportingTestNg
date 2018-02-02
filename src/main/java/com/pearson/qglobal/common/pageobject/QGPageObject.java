package com.pearson.qglobal.common.pageobject;

import org.openqa.selenium.WebDriver;

import com.pearson.common.pageobject.PageObject;
import com.pearson.global.common.factory.PageFactory;

public class QGPageObject extends PageObject {

    private static boolean AUTO_REGISTER = true;
    
    public QGPageObject(WebDriver driver) {
        super(driver);
        
        if(AUTO_REGISTER) {
            registerPageObject();
        }
    }
    
    /**
     * Usually we want to initialize the Factory and affliates by registering
     * explicitly - but for a price in performance, page-objects for QG can
     * self-register simply upon instantiation.
     * 
     * @param shouldRegisterAutomatically
     */
    public static void setAutoRegister(boolean shouldRegisterAutomatically) {
        AUTO_REGISTER = shouldRegisterAutomatically;
    }
    
    /**
     * A convenience routine to access the Page Factory for initialization.
     */
    @Override
    public void registerPageObject() {
        PageFactory.getInstance(driver).registerPageObject(this);
    }
    
   

	@Override
	public String getPageUrl() {
		// TODO Auto-generated method stub
		return null;
	}
}
