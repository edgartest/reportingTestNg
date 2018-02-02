package com.pearson.qglobal.factory;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.pearson.global.common.factory.QGPageObjectRegistrar;
import com.pearson.qglobal.common.pageobject.QGPageObject;

public class GenericRegistrar extends QGPageObjectRegistrar {
   
	public GenericRegistrar() {
    	super();
    }

    public void setupAllPageObjects(WebDriver driver) throws Exception {
        this.setup(Arrays.asList(QGPageObject.class));
        this.registerAllClasses(driver);
    }

    /**
     * Makes it so that we don't have to always edit the lists externally.
     * @throws Exception 
     */
    @Override
    public void setupDefaults(WebDriver driver) throws Exception {
        // Internally there is a check using instance-of so don't worry about
        // any IDE warnings about unchecked casts and type-safety.
        setup(Arrays.asList(QGPageObject.class // EVERYTHING
                            ));
        registerAllClasses(driver);
    }
    
    @Override
    public void setup(List<?> list) {
        
        for(Object c: list) {
            if(c instanceof Class<?>) {
                try {
                    this.addSubTypesOf((Class<?>)c);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
