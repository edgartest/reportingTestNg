package com.pearson.common;

import java.lang.reflect.Method;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.pearson.common.enums.CommonConstants;
import com.pearson.common.enums.QGUrl;
import com.pearson.common.exception.AutomationException;
import com.pearson.common.pageobject.PageObjectCreator;
import com.pearson.common.pageobject.PageObjectLoader;

public abstract class WebUITest {
	
	protected boolean initialized = false;
    protected String className= null;
    protected String methodName = null;
    protected String testName = null;
    protected JSONObject jsonConfig = null;
    protected String jsonFilePath = null;
    protected WebDriver driver = null;
    protected SeleniumWebDriver selenium = null;
    private boolean capturePageObjectSnapshots = true;
     
	protected <T1 extends Object> void initialize(Class<T1> testClassObject) throws Exception {
	        
		if(initialized) {
		    return;
		}
		
		initialized = true;
	        
		if(className == null) {
		    className = testClassObject.getName();
		    if(className.contains("$")) {
		        className = className.substring(0, className.lastIndexOf("$"));
		    }
		}
	
	//Provide ID for the testName thread
	if(testName == null) {
		if(testClassObject.getEnclosingMethod() == null) {
            throw new AutomationException("Failed to find a valid Enclosed Method for class object - use new Object(){}.getClass()",
                    driver);
        }
	    methodName = testClassObject.getEnclosingMethod().getName();
	    testName = methodName + "_T" + String.valueOf(Thread.currentThread().getId());
	}

	if(jsonFilePath == null) {
		
	    String packageStr = testClassObject.getPackage().getName();
	    if(packageStr.startsWith(CommonConstants.TEST_PACKAGE_PREFIX.getValue())) {
	        packageStr = packageStr.substring(CommonConstants.TEST_PACKAGE_PREFIX.getValue().length()+1);
	    }
	    
	    packageStr = packageStr.replace('.', CommonConstants.DIR_SEPARATOR.getValue().charAt(0));
	    jsonFilePath = packageStr.toLowerCase() + CommonConstants.DIR_SEPARATOR.getValue();
	    
	    initializeSelenium();
	    initializeLogger();
	    jsonFilePath = setupJson(jsonFilePath);
	    
	    Log.log(driver).info(CommonConstants.NEW_LINE.getValue() + CommonConstants.toStr());
	    
	    String output = CommonConstants.NEW_LINE.getValue() + "QaTest Parameters"
                + CommonConstants.NEW_LINE.getValue() + "\t[Class Name: " + className + "]"
                + CommonConstants.NEW_LINE.getValue() + "\t[Test Name: " + testName + "]"
                + CommonConstants.NEW_LINE.getValue() + "\t[Json Path: " + jsonFilePath + "]" 
                + CommonConstants.NEW_LINE.getValue() + "\t[Web Driver: " + driver.toString() + "]";
	    
	    Log.log(driver).info(output);
	    driver.get(QGUrl.LOGIN.getUrl());   
	}
	

	}
	
	 @SuppressWarnings("unchecked")
	protected <T1 extends Object, T2 extends PageObjectCreator> 
	   void initialize(Class<T1> testClassObject, Class<T2> pageObjectCreator) throws Exception {
	        
        if(initialized) {
            return;
        }
        
        initialize(testClassObject);
	        
	    // Initialize page factory settings.
	    Method method = pageObjectCreator.getMethod("getInstance", WebDriver.class);
	    T2 pageFactory = (T2) method.invoke(null, driver);
	    
	    pageFactory.setSnapshotOnCreation(isCapturePageObjectSnapshots());
	}
	 
	 protected <T1 extends Object, T2 extends PageObjectCreator, T3 extends PageObjectLoader> 
	    void initialize(Class<T1> testClassObject, Class<T2> pageObjectCreator, Class<T3> pageObjectLoader) throws Exception {
	        
        if(initialized) {
            return;
        }
        
        initialize(testClassObject, pageObjectCreator);
        
        // Start up the Page Object Factory by loading all known children
        // of the PageObject and related classes.
	    T3 loader = pageObjectLoader.newInstance();
	    loader.setupDefaults(driver);
	}

    protected void initializeSelenium() throws Exception {
        selenium = new SeleniumWebDriver();
        driver = selenium.initialize();
        SeleniumWebDriver.maximizeBrowserWindow(driver);
        Log.log(driver).info("Starting " + testName + "...");
    }
	
    protected void initializeLogger() throws Exception {
        Log.initialize(testName, driver);
        Log.replaceLogFormatter(driver);
        Log.replaceErrorLogFormatter(driver);
        Log.log(driver).info("Initialized loggers");
    }
	
	protected String setupJson(String folder) throws Exception {
		//Get file name
		String[] split = className.split("\\.");
		String name = split[split.length - 1];
		String jsonObjectStr =  Utils.firstLetterToLowerCase(name);
		String filePath = "json" + CommonConstants.DIR_SEPARATOR.getValue() + folder + jsonObjectStr + ".json";
		try {
		    jsonConfig = new JSONObject(Utils.readFileAsString(filePath));
		    jsonConfig = jsonConfig.getJSONObject(jsonObjectStr);
		} catch (Exception ex) {
			Log.log(driver).warning("JSON file doesn't exist");
			filePath = "WARNING JSON file doesn't exist";
		}
		return filePath;
	}

	/* ToDo: Create class to analyze and throw exception
	 * 
	 */
    protected void handleException(Throwable ex) throws Exception {
        //QGUtils.analyzeAndThrowException(driver, ex);
    	//throw new Exception(ex.getMessage());
    }
    
    public boolean isCapturePageObjectSnapshots() {
        return capturePageObjectSnapshots;
    }

    public void setCapturePageObjectSnapshots(boolean capturePageObjectSnapshots) {
        this.capturePageObjectSnapshots = capturePageObjectSnapshots;
    }
	
	
}
