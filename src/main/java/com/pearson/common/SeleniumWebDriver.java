package com.pearson.common;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.Platform;

import com.pearson.common.enums.CommonConstants;
import com.pearson.common.enums.Environment;
import com.pearson.common.exception.automation.AutomationInitializationException;
import com.pearson.common.exception.automation.WebDriverInitializationException;

public class SeleniumWebDriver {

	public static final String DEFAULT_BROWSER_TYPE = "firefox";
    public static final String DRIVER_DIR = CommonConstants.CURRENT_DIR.getValue() + "drivers" + CommonConstants.DIR_SEPARATOR.getValue();
    
    WebDriverConfiguration configuration = new WebDriverConfiguration();
    private InheritableThreadLocal<WebDriver> seleniumThread = new InheritableThreadLocal<WebDriver>();
    
    public SeleniumWebDriver() {
    }
    
    public SeleniumWebDriver(WebDriverConfiguration configuration) {
        if(configuration != null) {
            this.configuration = configuration;
        }
    }
    
    /**
     * Convenience routine that also initializes the logger and maximizes the 
     * screen automatically.
     * 
     * @param testName
     * @return
     * @throws Exception
     */
    public WebDriver initialize(String testName) throws Exception {
        
        WebDriver driver = null;
        
        try {
            driver = initialize();
            Log.getDefaultLogger().info(driver.toString());
            
        } catch (Exception ex) {
            throw new WebDriverInitializationException("Web Driver failed to initialize", ex);
        }
        
        Log.initialize(testName, driver);
        Log.log(driver).info(this.toString());
        Log.log(driver).info(CommonConstants.toStr());
        
        // We're going to replace defaults with the cleaner logger format.
        Log.replaceLogFormatter(driver);
        Log.replaceErrorLogFormatter(driver);
        
        // By default, let's avoid logging all web elements.
        //Utils.setLogWebElements(false);
        
        SeleniumWebDriver.maximizeBrowserWindow(driver);
        
        Log.log(driver).info("Starting " + testName + "...");
        return driver;
    }
    
    public static void maximizeBrowserWindow(WebDriver driver) throws Exception {
        Properties property = new Properties();
        String path;
        path = new java.io.File(".").getCanonicalPath();
        FileInputStream inputStream = new FileInputStream(path + CommonConstants.DIR_SEPARATOR.getValue() + "properties" + 
        									CommonConstants.DIR_SEPARATOR.getValue() + "config.properties");
        property.load(inputStream);
        boolean maximize = "true".equals(property.getProperty("maximize"));
        if (maximize) {
            driver.manage().window().maximize();
        }
    }
    
    /**
     * The meat of this class.  Initializes a number of objects and is the 
     * recommended focal point for any sort of web-driver related variations.
     * Allows the user to modify capabilities freely or override the driver 
     * instance.
     * 
     * @return
     * @throws Exception
     */
    public WebDriver initialize() throws Exception {
        Log.getDefaultLogger().info(configuration.toString());
        WebDriver driver = null;
        DriverType driverType = configuration.getDriverType();
               
        switch(driverType) {
            case CHROME:
                if (configuration.isLocal()) {
               File file = driverType.determineDriverLocation();
                    if(!file.exists()) {
                        throw new AutomationInitializationException(
                                "Chrome driver does not exist or is not executable: " 
                                        + file.toString());
                    }
                    String driverPath = file.getAbsolutePath();
                    System.setProperty("webdriver.chrome.driver", driverPath);
                    
                }
                break;
                
            case FIREFOX:
                if(configuration.isUsingBrowserProfile()) {
                    /*File profileDirectory = new File(DRIVER_DIR
                            + "firefox" + CommonConstants.DIR_SEPARATOR.getValue()
                            + "firefox_profile" + CommonConstants.DIR_SEPARATOR.getValue());*/
                	//FirefoxProfile profile = new FirefoxProfile(profileDirectory);
                	
                	ProfilesIni profileIni = new ProfilesIni();
            		FirefoxProfile profile = profileIni.getProfile("default");

                    DesiredCapabilities capabilities = configuration.getDesiredCapabilities();
                    
                    if(configuration.isUsingGrid()) {
                        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
                    } else {
                        // Previously was not creating profile via capabilities
                    	//driver = new FirefoxDriver(profile);
                         capabilities.setCapability(FirefoxDriver.PROFILE, profile);
                    }
                }
                else{
                	if (configuration.isLocal() && configuration.isUsingGecko()) {
                    	File file = driverType.determineDriverLocation();
                        if(!file.exists()) {
                            throw new AutomationInitializationException(
                                    "Gecko driver does not exist or is not executable: " 
                                            + file.toString());
                        }
                        String driverPath = file.getAbsolutePath();
                        System.setProperty("webdriver.gecko.driver", driverPath);
                	}
                	else{
                		if(configuration.isUsingGrid()) {
                        /*    File profileDirectory = new File(DRIVER_DIR
                                    + "firefox" + CommonConstants.DIR_SEPARATOR.getValue()
                                    + "firefox_profile" + CommonConstants.DIR_SEPARATOR.getValue());
                            
                            FirefoxProfile profile = new FirefoxProfile(profileDirectory);
                            DesiredCapabilities capabilities = configuration.getDesiredCapabilities();
                            
                            capabilities.setCapability(FirefoxDriver.PROFILE, profile);  */
                			System.out.println("using grid");
                            
                        }
                	}
                }
                break;
                
            case HTML_UNIT:
            	if (configuration.isLocal()) {
                	DesiredCapabilities capabilities = configuration.getDesiredCapabilities();
                	capabilities.setPlatform(Platform.WINDOWS);
                	capabilities.setVersion("1");
            	}

                break;
                
            case INTERNET_EXPLORER:
                if (configuration.isLocal()) {
                    File file = driverType.determineDriverLocation();
                    if(!file.exists() || !file.canExecute()) {
                        throw new AutomationInitializationException(
                                "Internet Explorer driver does not exist or is not executable: "
                                        + file.toString());
                    }
                    System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
                    
                    // http://jimevansmusic.blogspot.in/2012/08/youre-doing-it-wrong-protected-mode-and.html
                    // http://stackoverflow.com/questions/21324529/how-can-i-open-my-application-in-internet-explorer-11-0-in-selenium-web-driver
                    
                    DesiredCapabilities capabilities = configuration.getDesiredCapabilities();
                    capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                //    capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
                //    capabilities.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
                    
                } else {
                    
                    DesiredCapabilities capabilities = configuration.getDesiredCapabilities();
                    capabilities.setCapability("ignoreZoomSetting", true);
                    capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                    capabilities.setPlatform(Platform.WINDOWS);
                    
                    // TODO, FIXME: Figure out how to launch the driver!
                    //File file = new File("D:\\apps\\prod\\IEDriverServer.exe");
                    //System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
                    //InternetExplorerDriverService service = new InternetExplorerDriverService.Builder().withLogFile(new File("path-to-file")).withLogLevel(InternetExplorerDriverLogLevel.TRACE).build();
                    //driver = new InternetExplorerDriver(service, capabilities);
                }
                break;
                
            case SAFARI:
                break;
                
            case PHANTOM_JS:
                
                // We don't explicitly support PhantomJS so let it fall down
                // and raise an exception to the let the user know.
                //
                // capabilities.setCapability("takesScreenshot", true);
                // capabilities.setCapability("acceptSslCerts", true);
                // String defaultLocation = "/usr/bin/phantomjs";
                // if (browserString.contains("windows")) {
                //     defaultLocation = "C:\\apps\\prod\\phantomjs-1.9.1-windows\\phantomjs.exe";
                // }
                // capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, 
                //     System.getProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, defaultLocation));
                
            case UNKNOWN:
            default:
                throw new AutomationInitializationException(driverType.getName() + " is not supported");
        }
        
        if(driver == null) {
            
            // If it wasn't overridden and manually created in the switch 
            // statement above, then go ahead and create the web driver via 
            // normal means.
            
            driver = driverType.createWebDriver(configuration);
            driver.manage().timeouts().implicitlyWait(Long.valueOf(CommonConstants.ELEMENT_TIMEOUT.getValue()), TimeUnit.SECONDS);

        }

        // Adding a shutdown hook to ensure that Selenium is shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
        seleniumThread.set(driver);
        return driver;
    }
    
    public void shutdown() {
        try {
            WebDriver driver = seleniumThread.get();
            if(driver != null) {
                driver.close();
                driver.quit();
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        seleniumThread.remove();
        seleniumThread.set(null);
    }
    
    /**
     * A fancy enumeration type that helps sets defaults during initialization.
     * Implements a factory pattern for retrieving the enumeration in addition
     * to the web driver and desired capabilities instance.
     * 
     */
    static public enum DriverType {

        CHROME("chrome", 
                "chrome", 
                DesiredCapabilities.chrome(),
                ChromeDriver.class),
                
        FIREFOX("firefox", 
                "firefox", 
                DesiredCapabilities.firefox(),
                FirefoxDriver.class),
                
        HTML_UNIT("htmlunit", 
                "htmlunit", 
                DesiredCapabilities.htmlUnit(),
                HtmlUnitDriver.class),
                
        INTERNET_EXPLORER("iexplore", 
                "internet explorer", 
                DesiredCapabilities.internetExplorer(),
                InternetExplorerDriver.class),
                
        PHANTOM_JS("phantomjs", 
                "phantomjs", 
                DesiredCapabilities.phantomjs(),
                null /* PhantomJSDriver */ ), 
                
        SAFARI("safari", 
                "safari", 
                new DesiredCapabilities(),
                SafariDriver.class),
                
        UNKNOWN("unknown", 
                "unknown browser", 
                new DesiredCapabilities(),
                null);

        private String name;
        private String browserName;
        private DesiredCapabilities capabilities;
        private Class<? extends WebDriver> webDriverClass;
        
        DriverType(String name, String browserName, DesiredCapabilities capabilities, Class<? extends WebDriver> webDriverClass) {
            this.name = name.trim().toLowerCase();
            this.browserName = browserName;
            this.capabilities = capabilities;
            this.webDriverClass = webDriverClass;
        }

        public boolean contains(String name) {
            return this.name.contains(name);
        }
        
        public String getName() {
            return name;
        }
        
        public Class<? extends WebDriver> getWebDriverClass() {
            return webDriverClass;
        }
        
        /**
         * This returns a new instance of capabilities, uninitialized with 
         * just the defaults.
         * @see {@link SeleniumWrapper.WebDriverConfiguration#getDesiredCapabilities()}
         * @warning Don't use this to configure the desired capabilities.
         * @return
         * @throws Exception
         */
        public DesiredCapabilities createDesiredCapabilities() {
            // If this throw an exception - it's gotta be pretty fatal so a 
            // run-time exception makes sense.
            try {
                DesiredCapabilities desiredCapabilities = capabilities.getClass().newInstance();
                desiredCapabilities.setJavascriptEnabled(true);
                
                if(browserName != null && !browserName.isEmpty()) {
                    desiredCapabilities.setBrowserName(browserName);
                }
                return desiredCapabilities;
                
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        /**
         * Only supports Windoze, Mac and assumes Linux/*nix.  Defaults to win
         * 
         * @return The expected path to the driver.
         */
        public File determineDriverLocation() throws Exception {
            
            String osName = CommonConstants.OS_NAME.getValue();
            osName = osName.contains("win") ? "win" 
                    : osName.contains("mac") ? "mac" 
                    : osName.contains("nux") ? "linux" : "win";
            
            switch(this) {
                case CHROME:
                {
                    if(osName.contains("linux")) {
                        osName = appendArchSuffix(osName);
                    }
                    String filename = osName + appendExeSuffix("_chromedriver");
                    return new File(DRIVER_DIR + "chrome" + CommonConstants.DIR_SEPARATOR.getValue() + filename);
                }
                case INTERNET_EXPLORER:
                {
                    if(osName.contains("win")) {
                        osName = appendArchSuffix(osName).replace("x", "");
                    }
                    String filename = osName + appendExeSuffix("_IEDriverServer");
                    return new File(DRIVER_DIR + "ie" + CommonConstants.DIR_SEPARATOR.getValue() + filename);
                }
                case FIREFOX:
                {
                    if(osName.contains("win")) {
                        osName = appendArchSuffix(osName).replace("x", "");
                    }
                    String filename = osName + appendExeSuffix("_geckodriver");
                    return new File(DRIVER_DIR + "firefox" + CommonConstants.DIR_SEPARATOR.getValue() + filename);
                }
                default:
                    throw new AutomationInitializationException(
                            "This driver type does not have any associated driver files: " + this.toString());
            }
        }
        
        private String appendArchSuffix(String str) {
            return str + (CommonConstants.ARCH_TYPE.getValue().contains("64") ? "x64" : "x32");
        }
        
        private String appendExeSuffix(String str) {
            return str + (CommonConstants.OS_NAME.getValue().contains("win") ? ".exe" : "");
        }
        
        public WebDriver createWebDriver(WebDriverConfiguration config) throws Exception {
            if(config.usingGrid) {
                return createRemoteWebDriver(config);
            } else if(webDriverClass != null) {
                Constructor<?> constructors[] = webDriverClass.getConstructors();
                for(int i = 0; i < constructors.length; ++i) {
                    Class<?> params[] = constructors[i].getParameterTypes();
                    if(params != null && params.length == 1 && params[0].isAssignableFrom(Capabilities.class)) {
                        return (WebDriver)constructors[i].newInstance(config.getDesiredCapabilities());
                    }
                }
            }
            throw new AutomationInitializationException(
                    "Unable to construct an instance of web driver for " + this.toString());
        }
        
        public WebDriver createRemoteWebDriver(WebDriverConfiguration config) throws Exception{
            URL url = new URL("http", config.getRemoteHost(), config.getRemotePort(), "/wd/hub");
            return new RemoteWebDriver(url, config.getDesiredCapabilities());
        }
        
        @Override
        public String toString() {
            return super.toString()
                    + "[" + name + "-" + CommonConstants.OS_NAME.getValue() + "] "
                    + capabilities == null ? "" : capabilities.toString();
        }
        
        /**
         * The preferred way to instantiate the enumeration object.  This will
         * automatically peek at the environment (system properties) and create
         * one from expected parameters i.e. selenium.rc_type or will default
         * to DEFAULT_BROWSER_TYPE.
         * 
         * @return DriverType based on the system properties.
         */
        public static DriverType create() {
            return fromString(System.getProperty(CommonConstants.SELENIUM_BROWSER_TYPE_PROPERTY.getValue(), 
                                                 SeleniumWebDriver.DEFAULT_BROWSER_TYPE));
        }
        
        public static DriverType fromString(String text) {
            if (text != null) {
                text = text.replaceAll("\\s", "").toLowerCase();
                for (DriverType b : DriverType.values()) {
                    if (text.equals(b.getName())) {
                        return b;
                    }
                }
            }
            return UNKNOWN;
        }
    };
    
    /**
     * Encapsulates configuration parameters and helps coordinate the flow in
     * setting and getting values.  Standard stuff.
     * 
     */
    static public class WebDriverConfiguration {
        
        private String baseUrl = Environment.getBaseUrl();
        private Boolean usingBrowserProfile = Boolean.getBoolean(CommonConstants.SELENIUM_PROFILE_PROPERTY.getValue());
        private Boolean usingGrid = Boolean.getBoolean(CommonConstants.SELENIUM_GRID_PROPERTY.getValue());
        private Boolean usingGecko = Boolean.getBoolean(CommonConstants.SELENIUM_GECKO.getValue());
        private String remoteHost = System.getProperty(CommonConstants.SELENIUM_RC_HOST_PROPERTY.getValue(), "NONE");
        private Integer remotePort = Integer.getInteger(CommonConstants.SELENIUM_RC_PORT_PROPERTY.getValue(), 4444);
        private DriverType driverType = DriverType.create();
        private DesiredCapabilities desiredCapabilities = driverType.createDesiredCapabilities();
        
        public WebDriverConfiguration() {
        }

        @Override
        public String toString() {
            return super.toString()
                    + CommonConstants.NEW_LINE.getValue() + "\t[Type: " + driverType.toString() + "]"
                    + CommonConstants.NEW_LINE.getValue() + "\t[Capabilities: " + desiredCapabilities.toString() + "]"
                    + CommonConstants.NEW_LINE.getValue() + "\t[Base Url: " + baseUrl + "]"
                    + CommonConstants.NEW_LINE.getValue() + "\t[Grid: " + usingGrid + "]"
                    + CommonConstants.NEW_LINE.getValue() + "\t[Remote Host: " + remoteHost + "]"
                    + CommonConstants.NEW_LINE.getValue() + "\t[Remote Port: " + remotePort + "]"
                    + CommonConstants.NEW_LINE.getValue() + "\t[Profile: " + usingBrowserProfile + "]";
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public DriverType getDriverType() {
            return driverType;
        }

        public void setDriverType(DriverType driverType) {
            this.driverType = driverType;
        }

        public DesiredCapabilities getDesiredCapabilities() {
            return desiredCapabilities;
        }
        
        public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
            this.desiredCapabilities = desiredCapabilities;
        }
        
        public Boolean getUsingBrowserProfile() {
            return usingBrowserProfile;
        }

        public boolean isUsingBrowserProfile() {
            return usingBrowserProfile.booleanValue();
        }

        public void setUsingBrowserProfile(Boolean usingBrowserProfile) {
            this.usingBrowserProfile = usingBrowserProfile;
        }

        public Boolean getUsingGrid() {
            return usingGrid;
        }
        
        public boolean isUsingGrid() {
            return usingGrid.booleanValue();
        }

        public boolean isLocal() {
            return !isUsingGrid();
        }
        
        public void setUsingGrid(Boolean usingGrid) {
            this.usingGrid = usingGrid;
        }

        public void setUsingGecko(Boolean usingGecko) {
            this.usingGecko = usingGecko;
        }

        public boolean isUsingGecko() {
            return usingGecko.booleanValue();
        }
        
        public String getRemoteHost() {
            return remoteHost;
        }

        public void setRemoteHost(String remoteHost) {
            this.remoteHost = remoteHost;
        }

        public Integer getRemotePort() {
            return remotePort;
        }

        public void setRemotePort(Integer remotePort) {
            this.remotePort = remotePort;
        }
    }
}


