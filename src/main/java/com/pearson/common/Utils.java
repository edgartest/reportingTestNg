package com.pearson.common;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import com.pearson.common.enums.CommonConstants;
import com.pearson.common.enums.QGUrl;
import com.pearson.common.exception.DomNotCompleteException;
import com.pearson.common.exception.DomNotInteractiveException;
import com.pearson.common.exception.UnexpectedPageUrlException;
import com.pearson.common.exception.UrlNotLoadedException;
import com.pearson.common.exception.automation.WebElementNotChangedAttributeException;
import com.pearson.common.exception.automation.WebElementNotDisappearedException;
import com.pearson.common.exception.automation.WebElementNotFoundException;
import com.pearson.common.exception.automation.WebElementNotVisibleException;
import com.pearson.common.exception.uimessage.WebUiBlankPageException;
import com.pearson.common.exception.uimessage.WebUiErrorException;

public class Utils {
	
	static private Map<String, Utils> instances = new HashMap<String, Utils>();
	private WebDriver driver;
	static boolean logWebElements = false;
	static private boolean logPageSource = true;
	
	/*
	 * Reads file and converts to String
	 */
	public static String readFileAsString(String filePath) throws IOException {
		filePath = determineProjectPath() + filePath;
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
    /**
     * Defaults to "./" if there's an exception of any sort.
     * @return 
     */
    public static String determineProjectPath() {
        try {
            return (new File(".").getCanonicalPath()) + File.separator;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "." + File.separator;
    }
    
	/**
	 * Singleton instances are organized based on the driver instance passed in.
	 * 
	 * @param driver
	 * @return The Utils instance associated with the driver or null if it does
	 *         not exist.
	 */
	static public Utils getInstance(WebDriver driver) {

		if (driver == null) {
			return null;
		}

		Utils instance = null;
		String key = driver.toString();

		if (instances.containsKey(key)) {
			instance = instances.get(key);
		} else {
			instance = new Utils();
			instances.put(key, instance);
		}

		// Sync/copy the driver instance.
		if (instance.driver == null || !instance.driver.equals(driver)) {
			instance.driver = driver;
		}

		return instance;
	}
	
	/**
	 * Striaght from stack-overflow. Handy for converting constants to camel
	 * case.
	 * 
	 * @param str
	 * @return
	 */
	public static String toCamelCaseLowerCaseFirst(String str) {
		String[] parts = str.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + toProperCase(part);
		}
		camelCaseString = camelCaseString.substring(0, 1).toLowerCase()
				+ camelCaseString.substring(1);
		return camelCaseString;
	}
	
	public static String toCamelCase(String str) {
		String[] parts = str.split("_");
		String camelCaseString = "";
		for (String part : parts) {
			camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString;
	}

	public static String toProperCase(String str) {
		return str.substring(0, 1).toUpperCase()
				+ str.substring(1).toLowerCase();
	}
	
	public static String firstLetterToLowerCase(String str){
		return str.subSequence(0, 1).toString().toLowerCase() + str.substring(1);
	}

	public String getVersionId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void scrollToBottom(WebDriver driver) {
		((JavascriptExecutor) driver)
				.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
	}
	
	/**
	 * Waits until DOM readyState is assigned "complete".
	 * 
	 * @param driver
	 * @throws Exception
	 */
	
	public static void waitForDomComplete(WebDriver driver) throws Exception {
		waitForDomComplete(driver, null);
	}
	
	public static void waitForDomComplete(WebDriver driver, String errorMsg) throws Exception {
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			
			public Boolean apply(WebDriver driver) {
				Object obj = ((JavascriptExecutor) driver).executeScript("var result = document.readyState; return (result == 'complete');");

				if (obj == null) {
					Log.log(driver).warning("JavascriptExecutor object is null");
					return false;
				} else if (obj instanceof Boolean) {
					return obj.equals(true);
				} else if (obj instanceof String) {
					Log.log(driver).warning(
							"JavascriptExecutor object is an UNEXPECTED type "
									+ "[" + obj.getClass().toString() + "] ["
									+ obj.toString() + "]");
					return obj.equals("true");
				}

				Log.log(driver).warning(
						"JavascriptExecutor object is NOT recognized " + "["
								+ obj.getClass().toString() + "] ["
								+ obj.toString() + "]");

				return obj.equals(true);
			}
		};


		Wait<WebDriver> wait = new WebDriverWait(driver, 45);
		try {

			wait.until(condition);

		} catch (Exception ex) {

			Log.log(driver).warning("Dom ready-state check is taking a while, will continue with a less strict check");

			try {
				condition = new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver driver) {

						Object obj = ((JavascriptExecutor) driver).executeScript("return document.readyState;");

						if (obj == null) {
							Log.log(driver).warning("JavascriptExecutor object is null");
							return false;

						} else if (obj instanceof String) {
							if (obj.equals("complete")) {
								Log.log(driver).info("DOM ready-state is complete");
								return true;
							} else if (obj.equals("interactive")) {
								Log.log(driver).warning("DOM ready-state is interactive, will continue anyway");
								return true;
							}
							
							Log.log(driver).warning(
									"Failed waiting for DOM " + "["
											+ obj.getClass().toString() + "] ["
											+ obj.toString() + "]");
							return false;
						}

						Log.log(driver).warning(
								"JavascriptExecutor object is NOT recognized "
										+ "[" + obj.getClass().toString()
										+ "] [" + obj.toString() + "]");

						return (obj.equals("complete") || obj
								.equals("interactive"));
					}
				};

				wait = new WebDriverWait(driver, 15);
				wait.until(condition);

			} catch (Exception retryException) {
				String msg = (errorMsg == null || errorMsg.isEmpty()) ? "Failed to wait for DOM readyState"
						: "Failed to wait for DOM readyState, " + errorMsg;
				throw new DomNotCompleteException(msg, retryException, driver);
			}
		}
	}
	
	/**
	 * Waits until DOM readyState is assigned "complete" or "interactive", a
	 * less-strict version of {@link #waitForDomComplete(WebDriver)}.
	 * 
	 * @param driver
	 * @throws Exception
	 */
	public static void waitForDomInteractive(WebDriver driver) throws Exception {
		waitForDomInteractive(driver, null);
	}
	
	/**
	 * Only for internal use to help ready the page for element searches.
	 */
	private static boolean waitForDomInteractiveNoException(WebDriver driver)
			throws Exception {
		boolean isSuccessful = false;
		try {
			waitForDomInteractive(driver, null);
			isSuccessful = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isSuccessful;
	}
	
	public static void waitForDomInteractive(WebDriver driver, String warningMsg) throws Exception {
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				Object obj = ((JavascriptExecutor) driver).executeScript("var result = document.readyState; return (result == 'complete' || result == 'interactive');");

				if (obj == null) {
					Log.log(driver).warning("JavascriptExecutor object is null");
					return false;

				} else if (obj instanceof Boolean) {
					return obj.equals(true);

				} else if (obj instanceof String) {
					Log.log(driver).warning(
							"JavascriptExecutor object is an UNEXPECTED type "
									+ "[" + obj.getClass().toString() + "] ["
									+ obj.toString() + "]");
					return obj.equals("true");
				}

				Log.log(driver).warning(
						"JavascriptExecutor object is NOT recognized " + "["
								+ obj.getClass().toString() + "] ["
								+ obj.toString() + "]");

				return obj.equals(true);
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, 30);
		try {
			wait.until(condition);

		} catch (Exception ex) {
			String msg = (warningMsg == null || warningMsg.isEmpty()) ? "Failed to wait for DOM readyState"
					: "Failed to wait for DOM readyState, " + warningMsg;
			throw new DomNotInteractiveException(msg, ex, driver);
		}
	}
	
	/*
	 * An expectation for checking that an element is present on the DOM of a page.
	 */
	public static WebElement waitForElement(WebDriver driver, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(locator);
		WebElement element;
		try {
			element = wait.until(condition);
		} catch (Exception ex) {
			throw new WebElementNotFoundException("Failed to find element: " + locator.toString(), ex, driver);
		}
		return element;
	}

	/*
	 * An expectation for checking that an element is present on the DOM of a page and visible.
	 */
	public static WebElement waitForVisibleElement(WebDriver driver, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		ExpectedCondition<WebElement> condition = ExpectedConditions.visibilityOfElementLocated(locator);
		WebElement element;
		try {
			element = wait.until(condition);
		} catch (Exception ex) {
			throw new WebElementNotVisibleException("Failed to find element: " + locator.toString(), ex, driver);
		}
		return element;
	}

	/*
	 * An expectation for checking that an element is either invisible or not present on the DOM.
	 */
	public static boolean isVisible(WebDriver driver, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		try{
			wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
			return false;
		}
		catch(Exception ex){
			return true;
		}
		
	}
	
	/*
	 * An expectation for checking that an element is present on the DOM of a page. Inside a parentElement
	 */
	public static WebElement waitForElement(WebDriver driver, WebElement parent, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		final By location = locator;
		final WebElement parentElement = parent;
		ExpectedCondition<WebElement> condition = new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver driver) {
				return parentElement.findElement(location);
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, Long.parseLong(timeout));
		WebElement element;
		try {
			element = wait.until(condition);
		} catch (Exception ex) {
			throw new WebElementNotFoundException("Failed to find element: " + location.toString(), ex, driver);
		}
		return element;
	}

	/*
	 * An expectation for an element is not present on the DOM of a page.
	 */
	public static void waitForElementExtinct(WebDriver driver, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		final By location = locator;
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				List<WebElement> elements = driver.findElements(location);
				return elements.isEmpty();
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, Long.parseLong(timeout)).ignoring(
				NoSuchElementException.class).ignoring(
				StaleElementReferenceException.class);

		try {
			wait.until(condition);
		} catch (Exception ex) {
			throw new WebElementNotDisappearedException(
					"Failed to wait for element extinction: " + location.toString(), ex, driver);
		}
	}
	
	public static void waitForSpinner(WebDriver driver) throws Exception{
		Utils.waitForElementStyleNone(driver, By.xpath("//*[@id='spinnerModal']/ancestor::span[1]"), CommonConstants.ELEMENT_TIMEOUT.getValue());
	}
	
	public static void waitForGridLoad(WebDriver driver) throws Exception{
		Utils.waitForElementStyleNone(driver, By.xpath("//*[@id='spinnerModal']/ancestor::span[1]"), CommonConstants.ELEMENT_TIMEOUT.getValue());
	}
	
	public static void waitForElementStyleNone(WebDriver driver, final By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		try {
			wait.until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver driver) {
	            			 WebElement element = driver.findElement(locator);
	                         String style = element.getAttribute("style");
	                         if(style.contains("none")){
	                        	 return true;
	                         }
	                         else{
	                        	 return false;
	                         }       
	                    }
	   });
		} catch (Exception ex) {
			throw new WebElementNotDisappearedException("Failed to wait for element attribute dissapear: " + locator.toString(), ex, driver);
		}
	}
	
	public static void waitForAttribute(WebDriver driver, final By locator, final String attribute, final String expected, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		try {
			wait.until(new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver driver) {
	            			 WebElement element = driver.findElement(locator);
	                         String actAtt = element.getAttribute(attribute);
	                         if(actAtt.equals(expected)){
	                        	 return true;
	                         }
	                         else{
	                        	 return false;
	                         }
	                            
	                    }
	   });
		} catch (Exception ex) {
			throw new WebElementNotChangedAttributeException("Attribute: " + attribute + "Expecting: " + expected + locator.toString(), ex, driver);
		}
	}
	
	public static void waitForElementDisappear(WebDriver driver, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		final By location = locator;
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				List<WebElement> elements = driver.findElements(location);
				return (elements.isEmpty() || !hasDisplayedElement(driver, elements));
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, Long.parseLong(timeout)).ignoring(
				NoSuchElementException.class).ignoring(
				StaleElementReferenceException.class);

		try {
			wait.until(condition);
		} catch (Exception ex) {
			throw new WebElementNotDisappearedException("Failed to wait for element disappearance: " + location.toString(), ex, driver);
		}
	}
	
	/**
	 * Checks if all elements in the list are visible and swallows up any
	 * exceptions raised by stale elements in the DOM.
	 * 
	 * @param driver
	 * @param elements
	 * @return True if any single element is visible, otherwise false.
	 */
	public static <T extends WebElement> boolean hasDisplayedElement(WebDriver driver, List<T> elements) {
		boolean isDisplayed = false;
		for (T e : elements) {
			try {
				if (e.isDisplayed()) {
					isDisplayed = true;
					break;
				}
			} catch (StaleElementReferenceException ex) {
				Utils.handleExceptionWarning(driver, ex, false, false);
			}
		}
		return isDisplayed;
	}

	/**
	 * Checks if all elements in the list are visible.
	 * 
	 * @param elements
	 * @return True if any single element is visible, otherwise false.
	 */
	public static <T extends WebElement> boolean hasDisplayedElement(List<T> elements) {
		boolean isDisplayed = false;
		for (T e : elements) {
			if (e.isDisplayed()) {
				isDisplayed = true;
				break;
			}
		}
		return isDisplayed;
	}
	
	
	public static List<WebElement> waitForElements(WebDriver driver,
			By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		ExpectedCondition<List<WebElement>> condition = ExpectedConditions
				.presenceOfAllElementsLocatedBy(locator);
		List<WebElement> list = null;
		try {
			list = wait.until(condition);
		} catch (Exception ex) {
			throw new WebElementNotFoundException(locator.toString(), ex,
					driver);
		}
		return list;
	}

	public static List<WebElement> waitForAnyElements(WebDriver driver,
			By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		ExpectedCondition<List<WebElement>> condition = ExpectedConditions
				.presenceOfAllElementsLocatedBy(locator);
		List<WebElement> list = new ArrayList<WebElement>();
		try {
			list = wait.until(condition);
		} catch (Exception ex) {
		}
		return list;
	}

	public static List<WebElement> waitForAnyElements(WebDriver driver,
			WebElement parent, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		final By location = locator;
		final WebElement parentElement = parent;
		ExpectedCondition<List<WebElement>> condition = new ExpectedCondition<List<WebElement>>() {
			@Override
			public List<WebElement> apply(WebDriver driver) {
				return parentElement.findElements(location);
			}
		};
		List<WebElement> list = new ArrayList<WebElement>();
		try {
			list = wait.until(condition);
		} catch (Exception ex) {
		}
		return list;
	}

	public static List<WebElement> waitForVisibleElements(WebDriver driver, By locator, String timeout) throws Exception {
		waitForDomInteractiveNoException(driver);
		final By location = locator;
		WebDriverWait wait = new WebDriverWait(driver, Long.parseLong(timeout));
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				List<WebElement> elements = driver.findElements(location);
				return !elements.isEmpty() && hasDisplayedElement(elements);
			}
		};
		List<WebElement> list = null;
		try {
			wait.until(condition);
			list = driver.findElements(location);
		} catch (Exception ex) {
			throw new WebElementNotVisibleException(location.toString(), ex,
					driver);
		}

		return list;
	}

	/**
	 * Waits for a visible element - Helps avoid running into issues when trying
	 * to click on a web element.
	 * An expectation for checking that an element, known to be present on the DOM of a page, is visible.
	 * 
	 * @param driver
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public static boolean waitForVisibleElement(WebDriver driver, WebElement element) throws Exception {
		final WebElement webElement = element;
		ExpectedCondition<WebElement> condition = ExpectedConditions.visibilityOf(webElement);
		Wait<WebDriver> wait = new WebDriverWait(driver,
				Long.parseLong(CommonConstants.GLOBAL_DRIVER_TIMEOUT.getValue()));
		boolean isSuccessful = false;
		try {
			System.out.println("waiting visibility");
			wait.until(condition);
			isSuccessful = true;
		} catch (Exception ex) {
			throw new WebElementNotVisibleException("Failed to wait for element visibility: " + webElement.toString(), ex, driver);
		}
		return isSuccessful;
	}

	
	
	/**
	 * Handles an exception by recording it as a warning.
	 * 
	 * Note: This static method is capable of handling a null driver.
	 * 
	 * @see #handleException(WebDriver, Exception, boolean, boolean)
	 * @param driver
	 * @param ex
	 * @param createSnapshot
	 * @param printStackTrace
	 */
	public static void handleExceptionWarning(WebDriver driver, Exception ex, boolean createSnapshot, boolean printStackTrace) {
		if (ex == null) {
			Log.log(driver).severe("Exception is NULL");
			return;
		}

		StackTraceElement[] stackTrace = ex.getStackTrace();
		String errorMsg = "";

		if (printStackTrace) {
			errorMsg += CommonConstants.NEW_LINE.getValue();
			for (int i = 0; i < stackTrace.length; i++) {
				errorMsg += "\t" + stackTrace[i].toString()
						+ CommonConstants.NEW_LINE.getValue();
			}

			Throwable cause = ex.getCause();
			while (cause != null) {
				stackTrace = cause.getStackTrace();
				errorMsg += CommonConstants.NEW_LINE.getValue() + "Caused by: "
						+ cause.getMessage() + CommonConstants.NEW_LINE.getValue();
				for (int i = 0; i < stackTrace.length; i++) {
					errorMsg += "\t" + stackTrace[i].toString()
							+ CommonConstants.NEW_LINE.getValue();
				}
				cause = cause.getCause();
			}
		}

		String snapshot = "";
		try {
			if (createSnapshot) {
				String imageName = (ex.getClass().getSimpleName() == null ? "Warning"
						: (ex.getClass().getSimpleName() + "Warn"));

				snapshot = "Screenshot of warning: "
						+ captureScreenshot(driver, imageName, false)
						+ CommonConstants.NEW_LINE.getValue();
			}
		} catch (Exception exception) {
			snapshot = "WARNING, Failed to capure screenshot ["
					+ exception.getMessage() + "]";
			Log.log(driver).warning(
					"Caught exception: " + exception.getMessage());
			exception.printStackTrace();
		}

		String currentUrl = null;
		String exMessage = null;

		try {
			currentUrl = driver.getCurrentUrl();
		} catch (Exception exception) {
			Log.log(driver).warning(
					"Caught exception: " + exception.getMessage());
		}

		try {
			exMessage = ex.getMessage().replaceAll("\n", CommonConstants.NEW_LINE.getValue());
		} catch (Exception exception) {
			Log.log(driver).warning(
					"Caught exception: " + exception.getMessage());
		}

		errorMsg = "(" + (driver == null ? "NULL DRIVER" : driver.toString())
				+ ") " + "Warning Location: " + currentUrl + CommonConstants.NEW_LINE.getValue()
				+ CommonConstants.NEW_LINE.getValue() + snapshot + exMessage + errorMsg;

		Log.log(driver).warning(errorMsg);

		if (printStackTrace) {
			ex.printStackTrace();
		}
	}
	
	public static String captureScreenshotFail(WebDriver driver) {
		return captureScreenshot(driver, "Fail", false);
	}
	
	public static String captureScreenshotPass(WebDriver driver) {
		return captureScreenshot(driver, "Success", false);
	}
	
	public static String captureScreenshot(WebDriver driver,String imageDescription) {
		return captureScreenshot(driver, imageDescription, false);
	}
	
	public static String captureScreenshot(WebDriver driver) {
		return captureScreenshot(driver, null, true);
	}
	
	public static String captureScreenshot(WebDriver driver,String imageDescription, boolean shouldCompress) {

		if (imageDescription == null || imageDescription.isEmpty()) {
			imageDescription = "Snapshot";
		}

		// Remove all white-space characters.
		imageDescription = imageDescription.trim().replaceAll("\\s", "");

		String testName = Log.lookupTestName(driver);
		if (testName == null) {
			testName = "";
		}

		String filename = testName.concat("_")
				.concat(String.valueOf(System.currentTimeMillis())).concat("_")
				.concat(imageDescription);

		String strSeleniumGrid = CommonConstants.SELENIUM_GRID.getValue();
		if ("true".equalsIgnoreCase(strSeleniumGrid)) {
			driver = new Augmenter().augment(driver);
		}

		File scrFile = null;
		int attempt = 0;

		while (scrFile == null && attempt++ < 2) {
			try {
				scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			} catch (Exception ex) {
				if (driver == null) {
					Log.log(driver).warning("Screenshot failed, driver is null");
					break;
				}
				Log.log(driver).warning("Screenshot failed, attempting retry");
				Utils.sleep(250);
			}
		}

		String destPngFilename = Log.FOLDER_PATH + CommonConstants.DIR_SEPARATOR.getValue() + filename + ".png";
		File destinationPng = new File(destPngFilename);

		Integer compressionLevel = shouldCompress ? 10 : 4;

		// Here are all of the ways in which we can help reduce the file size.
		//
		// Option 1: Resize the image (scale it down).
		// Option 2: Use PNG-Tastic to compress, seems to work quite well.
		// Option 3: Using javax's ImageIO (slightly better than the default).
		// Option 4: Use Selenium's default, which is highly totally lossless.

		try {

			PngOptimizer optimizer = new PngOptimizer("error");
			optimizer.setCompressor("",null);

			try {

				// Resizing might take a LOT longer than expected, will need
				// to keep a close eye on this.

				if (shouldCompress) {
/*
					// Scoped for explicitly ensuring we're dealing with the
					// image in only a limited context.

					{
						BufferedImage img = ImageIO.read(scrFile);
						BufferedImage bdest = new BufferedImage(
								(int) (img.getWidth() * 0.66),
								(int) (img.getHeight() * 0.66),
								BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = bdest.createGraphics();
						g.drawImage(img, 0, 0, (int) (img.getWidth() * 0.66),
								(int) (img.getHeight() * 0.66), null);
						g.dispose();
						ImageIO.write(bdest, "png", destinationPng);

						img.flush();
						bdest.flush();

						// See notes below on garbaage collectiones.

						g = null;
						bdest = null;
						img = null;
					} */

					PngImage image = new PngImage(scrFile.getAbsolutePath(),null);
					optimizer.optimize(image, destPngFilename, false,compressionLevel);

					// SIGH, just cause there's garbage collection doesn't mean
					// everything gets purged immediately. Set these to null
					// to indicate to the JVM we want these disposed of.

					image = null;
					Log.log(driver).info("Screenshot Success File: "+testName);

				} else {
					// Even though shouldCompress is "false", we're going to
					// do some very light compression on it anyway.
					PngImage image = new PngImage(scrFile.getAbsolutePath(),null);
					optimizer.optimize(image, destPngFilename, false, compressionLevel);
					image = null;
					Log.log(driver).info("Screenshot Success File: "+testName);
				}

				// Initially suspected Pngtastic was the memory hog, turns out
				// that ImageIO is also suspect - so we'll leave this here to
				// indicate that anyway.
				
				optimizer = null;

			} catch (Exception e) {
				Log.log(driver).warning("Failed to compress file, will default to ImageIO");
				BufferedImage img = ImageIO.read(scrFile);
				ImageIO.write(img, "png", destinationPng);
				img = null;
			}

		} catch (Exception ex) {
			Log.log(driver).warning(ex.getMessage());

			try {
				FileUtils.copyFile(scrFile, destinationPng);
			} catch (Exception e) {
				Log.log(driver).severe(e.getMessage());
			}
		}
		return destPngFilename;
	}

	
	/**
	 * Swallows exceptions, use with care. This is discouraged, please try to
	 * use wait methods instead.
	 * 
	 * @param millis
	 *            Time to sleep in milliseconds (1sec = 1000ms)
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getPageSyncObject(WebDriver driver, final String timeout) throws Exception {
		System.out.println("Executing Javascript");
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				Object obj = ((JavascriptExecutor) driver).executeScript(
						"function getPageSyncObject(timeout){ "
						+ "var loadingMsg = Array.prototype.slice.call(document.getElementsByClassName('loading-message')); "
						+ "var pageSyncObject = { "
						+ "	isTimedOut: false, " 
						+ "	isComplete: function(){ "
						+ "	  if(this.isTimedOut) "
						+ "	    return true; "
						+ "	var len = loadingMsg.filter(function(ele){ "
						+ "			return (ele.offsetWidth > 0 && ele.offsetHeight > 0); "
						+ "		}).length; "
						+ "	if(len===0){ "
						+ "		clearTimeout(sto); "
						+ "		return true; "
						+ "	} "
						+ "	return false; "
						+ "	} "
						+ "}; "
						+ "var sto = setTimeout(function(){ "
						+ "	pageSyncObject.isTimedOut=true "
						+ "}, timeout); "
						+ "return pageSyncObject; "
						+ "};"
						+ "getPageSyncObject("+timeout+");");
				
				System.out.println(obj.toString());

				if (obj == null) {
					Log.log(driver).warning("JavascriptExecutor object is null");
					return false;

				} else if (obj instanceof Boolean) {
					return obj.equals(true);

				} else if (obj instanceof String) {
					Log.log(driver).warning(
							"JavascriptExecutor object is an UNEXPECTED type "
									+ "[" + obj.getClass().toString() + "] ["
									+ obj.toString() + "]");
					return obj.equals("true");
				}

				Log.log(driver).warning(
						"JavascriptExecutor object is NOT recognized " + "["
								+ obj.getClass().toString() + "] ["
								+ obj.toString() + "]");

				return obj.equals(true);
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, 30);
		try {
			wait.until(condition);

		} catch (Exception ex) {
			String msg = "Failed to wait for DOM Spinner";
			throw new DomNotInteractiveException(msg, ex, driver);
		}
	}

	public static void scrollToElement(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public static Actions moveToAndClick(WebDriver driver, WebElement element) throws Exception {
		scrollToElement(driver, element);
		Actions actions = new Actions(driver);
		try {
			try {
				actions.moveToElement(element).click().perform();

			} catch (MoveTargetOutOfBoundsException ex) {
				Log.log(driver).warning(ex.getMessage());
				// If we're having problems moving to the element, just
				// ignore it and try to click.
				element.click();
			}
		} catch (WebDriverException ex) {

			if (ex.getMessage() != null
					&& ex.getMessage().contains("dead object")) {
				// Trying to handle the "can't access dead object error". =(.
				// https://code.google.com/p/selenium/issues/detail?id=7637
				Utils.scrollTo(driver, element);
				element.click();

			} else {
				throw ex;
			}
		}
		return actions;
	}
	
	/**
	 * Unlike {@link Utils#scrollToElement(WebDriver, WebElement)}, this will
	 * scroll to the elment regardless of the browser type.
	 * 
	 * @param driver
	 * @param element
	 */
	public static void scrollTo(WebDriver driver, WebElement element) {
		int yOffset = ((Locatable) element).getCoordinates().onPage().getY();
		((JavascriptExecutor) driver).executeScript("window.scrollBy(0," + yOffset + ");");
	}

	public static Actions sendKeys(WebDriver driver, WebElement element, CharSequence keysToSend) throws Exception {
		scrollToElement(driver, element);
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().sendKeys(keysToSend).perform();
		return actions;
	}
	
	public static Actions clearAndSendKeys(WebDriver driver,
		WebElement element, CharSequence keysToSend) throws Exception {
		scrollToElement(driver, element);
		Actions actions = new Actions(driver);
		element.clear();
		actions.moveToElement(element).click().sendKeys(keysToSend).perform();
		return actions;
	}

	public static void handleException(WebDriver driver, Exception ex, boolean createSnapshot, boolean printStackTrace,
			boolean printCauseToLog) {
		if (ex == null) {
			Log.log(driver).severe("Exception is NULL");
			return;
		}

		StackTraceElement[] stackTrace = ex.getStackTrace();

		String logMsg = ex.getClass().getName() + ": " + ex.getMessage();
		String errorMsg = "";

		if (printStackTrace) {

			errorMsg += CommonConstants.NEW_LINE.getValue();

			for (int i = 0; i < stackTrace.length; i++) {
				errorMsg += "\t" + stackTrace[i].toString()
						+ CommonConstants.NEW_LINE.getValue();
			}

			Throwable cause = ex.getCause();
			while (cause != null) {
				stackTrace = cause.getStackTrace();
				errorMsg += CommonConstants.NEW_LINE.getValue() + "Caused by: "
						+ cause.getMessage() + CommonConstants.NEW_LINE.getValue();
				for (int i = 0; i < stackTrace.length; i++) {
					errorMsg += "\t" + stackTrace[i].toString()
							+ CommonConstants.NEW_LINE.getValue();
				}

				if (cause.getMessage() != null) {
					if (printCauseToLog) {
						logMsg += CommonConstants.NEW_LINE.getValue() + "\tCaused by: "
								+ cause.getMessage();
					}
				}
				cause = cause.getCause();
			}

		} else {
			Throwable cause = ex.getCause();
			while (cause != null) {
				if (cause.getMessage() != null) {
					if (printCauseToLog) {
						logMsg += CommonConstants.NEW_LINE.getValue() + "\tCaused by: "
								+ cause.getMessage();
					}
				}
				cause = cause.getCause();
			}
		}

		// for(Throwable t : ex.getSuppressed()) {
		// if(t == null) continue;
		// stackTrace = t.getStackTrace();
		// errorMsg += "\nSuppressed: " + t.getMessage() + Constants.NEW_LINE;
		// for (int i = 0; i < stackTrace.length; i++) {
		// errorMsg += "\t" + stackTrace[i].toString() + Constants.NEW_LINE;
		// }
		// }

		String snapshot = "";
		String imageFilename = "";

		try {
			if (createSnapshot) {
				String imageDescription = (ex.getClass().getSimpleName() == null ? "Error"
						: ex.getClass().getSimpleName());
				imageFilename = captureScreenshot(driver, imageDescription,
						false);
				snapshot = "Screenshot of error: " + imageFilename
						+ CommonConstants.NEW_LINE.getValue();
			}

		} catch (Exception exception) {
			snapshot = "WARNING, Failed to capure screenshot ["
					+ exception.getMessage() + "]";
			Log.log(driver).warning(
					"Caught exception: " + exception.getMessage());
			exception.printStackTrace();
		}

		String currentUrl = null;
		String exMessage = null;

		try {
			currentUrl = driver.getCurrentUrl();
		} catch (Exception exception) {
			Log.log(driver).warning(
					"Caught exception: " + exception.getMessage());
		}

		try {
			exMessage = ex.getMessage().replaceAll("\n", CommonConstants.NEW_LINE.getValue());
		} catch (Exception exception) {
			Log.log(driver).warning(
					"Caught exception: " + exception.getMessage());
		}

		String conversationId = "NONE";
		Utils utils = Utils.getInstance(driver);
		if (utils != null) {
			conversationId = utils.getVersionId();
		}

		errorMsg = "(" + (driver == null ? "NULL DRIVER" : driver.toString())
				+ ") " + "Error Location: " + currentUrl + " [[ConvoId:"
				+ conversationId + "]]" + CommonConstants.NEW_LINE.getValue()
				+ CommonConstants.NEW_LINE.getValue() + snapshot + exMessage + errorMsg;

		Log.log(driver).severe(logMsg);
		Log.errorLog(driver).severe(errorMsg);

		if (printStackTrace) {
			Log log = Log.getLog(driver);
			if (log != null) {
				// We want to avoid printing out to stream if it's already going
				// to
				// console by the regular call with the loggers.
				if (!log.isUsingStdStreamOut()) {
					ex.printStackTrace();
				}
			} else {
				// If log is null, then lets be on the safe side and print it
				// out.
				ex.printStackTrace();
			}
		}

		try {

			// Avoid recording the page source for Web UI related errors that
			// already display an error message.
			//
			// The intent of this is to provide a snapshot of the DOM (partial)
			// to discover error message containers and also provides a
			// potentially faster way of identifying x-path changes.

			if (logPageSource && !(ex instanceof WebUiErrorException)
					&& !(ex instanceof UnexpectedPageUrlException)
					&& !(ex instanceof SQLException)) {

				// Since the image files are generally regarded as unique,
				// we'll piggy-back off of that and use it to name the
				// associated html dump as well. It should be extremely easy
				// to match up in the Bamboo artifact log folder.

				if (!imageFilename.isEmpty()) {

					// Writing the page source to a file in order to prevent
					// flooding the error log.

					String htmlFilename = imageFilename.concat(".txt");
					BufferedWriter out = new BufferedWriter(new FileWriter(
							htmlFilename));
					out.write(driver.getPageSource());
					out.close();

				} else {

					// Finally, if we can't write to a file then let's just
					// stuff it into the log file.

					Log.errorLog(driver).info(
							CommonConstants.NEW_LINE.getValue() + driver.getPageSource()
									+ CommonConstants.NEW_LINE.getValue());
				}
			}

		} catch (Exception exception) {
		}
		
	}

	public static void handleException(WebDriver driver, Exception ex) {
		handleException(driver, ex, true, true, false);
	}
	
	public static void waitForUrlLoaded(WebDriver driver, String url) throws Exception {
		waitForUrlLoaded(driver, url, Long.parseLong(CommonConstants.GLOBAL_DRIVER_TIMEOUT.getValue()));
	}

	public static void waitForUrlLoaded(WebDriver driver, String url, long timeout) throws Exception {
		if (url.isEmpty()) {
			throw new UrlNotLoadedException("Url is empty", driver);
		}

		final String urlStr = url;

		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				String driverUrl = driver.getCurrentUrl();
				int index = driverUrl.indexOf('?');
				driverUrl = (index > -1) ? driverUrl.substring(0, index)
						: driverUrl;
				return urlStr.equals(driverUrl);
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, timeout);
		try {
			wait.until(condition);
		} catch (Exception ex) {
			throw new UrlNotLoadedException("Failed to verify driver URL: "
					+ url + " vs " + driver.getCurrentUrl(), ex, driver);
		}
	}
	
	public static void waitForUrlLoaded(WebDriver driver, QGUrl url) throws Exception {
		waitForUrlLoaded(driver, url, Long.getLong(CommonConstants.GLOBAL_DRIVER_TIMEOUT.getValue()));
	}

	public static void waitForUrlLoaded(WebDriver driver, QGUrl url, long timeout) throws Exception {
		if (!url.isValid()) {
			throw new UrlNotLoadedException(url.toString(), driver);
		}

		final String urlStr = url.getUrl();

		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				String driverUrl = driver.getCurrentUrl();
				int index = driverUrl.indexOf('?');
				driverUrl = (index > -1) ? driverUrl.substring(0, index)
						: driverUrl;
				return urlStr.equals(driverUrl);
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, timeout);
		try {
			wait.until(condition);
		} catch (Exception ex) {
			throw new UrlNotLoadedException("Failed to verify driver URL with "
					+ url.toString() + ". Current url is "
					+ driver.getCurrentUrl(), ex, driver);
		}
	}
	
	public static void waitForUrlContains(WebDriver driver, QGUrl url) throws Exception {
		final String urlStr = url.getUrl();
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return driver.getCurrentUrl().contains(urlStr);
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, Long.parseLong(CommonConstants.GLOBAL_DRIVER_TIMEOUT.getValue()));
		try {
			wait.until(condition);
		} catch (Exception ex) {
			throw new UrlNotLoadedException("Failed to verify driver URL with "
					+ url.toString() + ". Current url is "
					+ driver.getCurrentUrl(), ex, driver);
		}
	}

	/**
	 * Grabs any text contained inside of each of these error-related elements.
	 * 
	 * @param elements
	 * @return Extracts the text and returns a formatted string from the arg.
	 */
	public static String extractText(List<WebElement> elements) {

		String msg = "";
		try {

			for (WebElement e : elements) {
				if (e.getText() != null && !e.getText().isEmpty()) {
					msg += "\n\t" + e.getText();
				}
			}
		} catch (Exception ex) {
			// We actually risk running into stale-elements, as always.
			ex.printStackTrace();
		}

		return msg;
	}
	
	public static void checkForBlankPage(WebDriver driver) throws Exception {
		checkForBlankPage(driver, true);
	}

	public static boolean checkForBlankPage(WebDriver driver, boolean raiseException) throws Exception {

		// If there isn't a body-tag or if the body doesn't have child nodes
		// then this is a blank page.

		Object result = ((JavascriptExecutor) driver)
				.executeScript("var list = document.documentElement.getElementsByTagName(\"body\");"
						+ "return ((list.length > 0) ? !list[0].hasChildNodes() : true);");

		boolean isBlankPage = false;

		if (result instanceof Boolean) {
			isBlankPage = (Boolean) result;

			if (raiseException && isBlankPage) {
				throw new WebUiBlankPageException(driver);
			}

		} else {
			Log.log(driver).warning("JavaScript result to check for blank-page FAILED to return a boolean value");
		}

		return isBlankPage;
	}
	

}
