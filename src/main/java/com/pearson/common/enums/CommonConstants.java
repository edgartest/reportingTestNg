package com.pearson.common.enums;

import java.io.File;

import com.pearson.common.Utils;

public enum CommonConstants {
	DIR_SEPARATOR(Constants.DIR_SEPARATOR),
	TEST_PACKAGE_PREFIX(Constants.TEST_PACKAGE_PREFIX),
	TEST_PACKAGE_PREFIX_FILE_SEPARATOR(Constants.TEST_PACKAGE_PREFIX_FILE_SEPARATOR),
	CURRENT_DIR(Constants.CURRENT_DIR),
	NEW_LINE(Constants.NEW_LINE),
	SELENIUM_GECKO_DRIVER_PROPERTY(Constants.SELENIUM_GECKO_DRIVER_PROPERTY),
	LINE_SEPARATOR(Constants.LINE_SEPARATOR),
	SELENIUM_PROFILE_PROPERTY(Constants.SELENIUM_PROFILE_PROPERTY),
	SELENIUM_GRID_PROPERTY(Constants.SELENIUM_GRID_PROPERTY),
	SELENIUM_XML_FILESET_PROPERTY(Constants.SELENIUM_XML_FILESET_PROPERTY),
	SELENIUM_RC_HOST_PROPERTY(Constants.SELENIUM_RC_HOST_PROPERTY),
	SELENIUM_RC_PORT_PROPERTY(Constants.SELENIUM_RC_PORT_PROPERTY),
	SELENIUM_BROWSER_TYPE_PROPERTY(Constants.SELENIUM_BROWSER_TYPE_PROPERTY),
	OS_NAME(Constants.OS_NAME),
	OS_ARCH_TYPE(Constants.OS_ARCH_TYPE),
	ARCH_TYPE(Constants.ARCH_TYPE),
	SELENIUM_DEFAULT_TIMEOUT_PROPERTY(Constants.SELENIUM_DEFAULT_TIMEOUT_PROPERTY),
	SELENIUM_ELEMENT_TIMEOUT_PROPERTY(Constants.SELENIUM_ELEMENT_TIMEOUT_PROPERTY),
	SELENIUM_PAGE_TIMEOUT_PROPERTY(Constants.SELENIUM_PAGE_TIMEOUT_PROPERTY),
	GLOBAL_DRIVER_TIMEOUT(Constants.GLOBAL_DRIVER_TIMEOUT),
	ELEMENT_TIMEOUT(Constants.ELEMENT_TIMEOUT),
	PAGE_TIMEOUT(Constants.PAGE_TIMEOUT),
	TEST_ENVIRONMENT(Constants.TEST_ENVIRONMENT),
	SELENIUM_GRID(Constants.SELENIUM_GRID),
	SELENIUM_GECKO(Constants.SELENIUM_GECKO),
	JSON_PATH(Constants.JSON_PATH),
	PROJECT(Constants.PROJECT);
	
	private final String value;
	
    private CommonConstants(String value) {
        this.value = value;
    }
        
	public String getValue(){
		return value;
	}
	
	private static class Constants {
		public static final String DIR_SEPARATOR = File.separator;
		public static final String TEST_PACKAGE_PREFIX = "com.pearson.test";
		public static final String TEST_PACKAGE_PREFIX_FILE_SEPARATOR = TEST_PACKAGE_PREFIX.replace(".", DIR_SEPARATOR);
		public static final String CURRENT_DIR = Utils.determineProjectPath();
		public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
		public static final String NEW_LINE = LINE_SEPARATOR;
		public static final String SELENIUM_GECKO_DRIVER_PROPERTY = "selenium.gecko";
		public static final String SELENIUM_PROFILE_PROPERTY = "firefox.profile";
		public static final String SELENIUM_GRID_PROPERTY = "selenium.grid";
		public static final String SELENIUM_XML_FILESET_PROPERTY ="selenium.xmlfileset";
		public static final String SELENIUM_RC_HOST_PROPERTY = "selenium.rc_host";
		public static final String SELENIUM_RC_PORT_PROPERTY = "selenium.rc_port";
		public static final String SELENIUM_BROWSER_TYPE_PROPERTY = "selenium.browser";
		public static final String OS_NAME = System.getProperty("os.name", "ERROR").toLowerCase();
		public static final String OS_ARCH_TYPE = System.getProperty("os.arch", "ERROR");
		public static final String ARCH_TYPE = System.getProperty("sun.arch.data.model", "ERROR");
		public static final String SELENIUM_DEFAULT_TIMEOUT_PROPERTY = "selenium.default_timeout";
	    public static final String SELENIUM_ELEMENT_TIMEOUT_PROPERTY = "selenium.element_timeout";
	    public static final String SELENIUM_PAGE_TIMEOUT_PROPERTY = "selenium.page_timeout";
	    public static final String GLOBAL_DRIVER_TIMEOUT = Integer.getInteger(SELENIUM_DEFAULT_TIMEOUT_PROPERTY, 60).toString();
	    public static final String ELEMENT_TIMEOUT = Integer.getInteger(SELENIUM_ELEMENT_TIMEOUT_PROPERTY, 30).toString();
	    public static final String PAGE_TIMEOUT = Integer.getInteger(SELENIUM_PAGE_TIMEOUT_PROPERTY, 30).toString();
		public static final String TEST_ENVIRONMENT = Environment.getName();
		public static final String SELENIUM_GRID = String.valueOf(Boolean.getBoolean(SELENIUM_GRID_PROPERTY));
		public static final String SELENIUM_GECKO = String.valueOf(Boolean.getBoolean(SELENIUM_GECKO_DRIVER_PROPERTY));
		public static final String JSON_PATH = "json";
		public static final String PROJECT = "QG";
	}
	
    /**
     * For general debugging. Constructs a string of values that are set via
     * properties (e.g. JVM's -D args).
     * @return str 
     */
    public static String toStr() {
        String str = "Static variables" +
        		"\n\tTEST_ENVIRONMENT [" + TEST_ENVIRONMENT.getValue() + "]" +
        		"\n\tSELENIUM_GRID [" + SELENIUM_GRID.getValue() + "]" +
                "\n\tGLOBAL_DRIVER_TIMEOUT [" + GLOBAL_DRIVER_TIMEOUT.getValue() + "]" +
                "\n\tELEMENT_TIMEOUT [" + ELEMENT_TIMEOUT.getValue() + "]" +
                "\n\tPAGE_TIMEOUT [" + PAGE_TIMEOUT.getValue() + "]" +
                "";
        return str;
    }
	
}
