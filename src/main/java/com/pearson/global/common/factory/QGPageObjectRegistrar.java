package com.pearson.global.common.factory;

import java.util.List;

import org.openqa.selenium.WebDriver;

import com.pearson.common.pageobject.PageObjectRegistrar;
import com.pearson.qglobal.common.pageobject.QGPageObject;

public class QGPageObjectRegistrar extends PageObjectRegistrar {

    public static final String QG_ROOT_PACKAGE = DEFAULT_ROOT_PACKAGE + ".wdw";

    public QGPageObjectRegistrar() {
        // We're going to narrow down the scope of the searches to improve 
        // start-up time - previously seen up to 900 milliseconds to load.
        super(QG_ROOT_PACKAGE);
    }

    protected QGPageObjectRegistrar(String packagePath) {
        super(packagePath);
    }

    /**
     * Helps add an extra guard by filtering out by WdwPageObject class type.
     */
    @Override
    public void registerAllClasses(WebDriver driver) throws Exception {
        registerAllClasses(QGPageObject.class, driver);
    }

	@Override
	public void setupDefaults(WebDriver driver) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setup(List<?> list) {
		// TODO Auto-generated method stub
		
	}
}
