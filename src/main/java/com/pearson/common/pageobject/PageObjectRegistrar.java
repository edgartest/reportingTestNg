package com.pearson.common.pageobject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.openqa.selenium.WebDriver;
import org.reflections.Reflections;

import com.pearson.common.exception.automation.PageObjectCreatorNotRecognizedException;

import java.util.ArrayList;
import java.util.List;

public abstract class PageObjectRegistrar implements PageObjectLoader {
	
	public static final String DEFAULT_ROOT_PACKAGE = "com.pearson";
    
    private List<Class<?>> classes = new ArrayList<Class<?>>();
    private Reflections reflections = null;
    
    public PageObjectRegistrar() {
        reflections = new Reflections(DEFAULT_ROOT_PACKAGE);
    }
    
    public PageObjectRegistrar(String packagePath) {
        reflections = new Reflections(packagePath);
    }
    
    public boolean add(Class<?> c) {
        return classes.add(c);
    }
    
    /**
     * Uses the reflections library to auto-magically find and stores classes 
     * for registration.
     * 
     * @see <a href="https://code.google.com/p/reflections/">Reflections Lib</a>
     * @param packagePath
     * @return
     * @throws Exception
     */
    public boolean addSubTypesOf(Class<?> type) throws Exception {
        
        boolean isSuccessful = false;
        
        for(Class<?> c: reflections.getSubTypesOf(type)) {
            add(c);
        }
        
        return isSuccessful;
    }
    
    @Override
    public void registerAllClasses(WebDriver driver) throws Exception {
        registerAllClasses(PageObject.class, driver);
    }
    
    @Override
    public void registerAllClasses(Class<? extends PageObject> pageObjectClass, WebDriver driver) throws Exception {
        
        for(Class<?> c : classes) {
            
            // Check that this class is assignable (a subclass) and make sure
            // we skip any interfaces and abstract classes.
            if(pageObjectClass.isAssignableFrom(c) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                Constructor<?> list[] = c.getConstructors();
                
                if(list.length < 1) {
                    continue;
                }
                
                Constructor<?> construct = list[0];
                Class<?> params[] = construct.getParameterTypes();
                
                if(params.length > 0 && params[0].isAssignableFrom(WebDriver.class)){
                    
                    PageObject pageObject = (PageObject) list[0].newInstance(driver);
                    if(pageObject != null) {
                        pageObject.registerPageObject();
                    }
                    
                } else if(params.length > 1 && params[1].isAssignableFrom(WebDriver.class)) {
                    
                    // Handle nested classes.  DOH.  =).
                    //
                    // The first parameter is the hosting class and the second
                    // parameter should be the web-driver.
                    
                    Object obj = params[0].getConstructors()[0].newInstance();
                    PageObject pageObject = (PageObject) list[0].newInstance(obj, driver);
                    if(pageObject != null) {
                        pageObject.registerPageObject();
                    }
                    
                } else {
                    
                    // Parameters don't match anything we expect so throw an
                    // exception that points to the offending constructor.
                    
                    throw new PageObjectCreatorNotRecognizedException(construct.toGenericString(), driver);
                }
            }
        }
    }
}
