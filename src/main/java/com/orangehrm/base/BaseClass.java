package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utils.ExtentManager;
import com.orangehrm.utils.LoggerManager;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	// ======================================================
	// GLOBAL VARIABLES
	// ======================================================

	protected static Properties prop;

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();


	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	// ======================================================
	// LOAD CONFIGURATION FILE
	// ======================================================

	@BeforeSuite
	public void loadConfig() throws IOException {

		prop = new Properties();

		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");

		prop.load(fis);

		logger.info("Configuration properties file loaded successfully");
	}

	// ======================================================
	// TEST SETUP
	// ======================================================

	@BeforeMethod
	@Parameters("browser")
	public synchronized void setup(String browser) {

		logger.info("========== TEST EXECUTION STARTED ==========");
		
		// Store browser for report usage
	    ExtentManager.setBrowser(browser);
	    
		launchBrowser(browser);

		configBrowser();

		// Initialize ActionDriver
		actionDriver.set(new ActionDriver(getDriver()));

		logger.info("ActionDriver initialized successfully");
	}

	// ======================================================
	// BROWSER LAUNCH
	// ======================================================

	private synchronized void launchBrowser(String browser) {

		// String browser = prop.getProperty("browser"); -- This is passed from the
		// TestNG from run time

		boolean headless = Boolean.parseBoolean(prop.getProperty("headless"));

		logger.info("Launching browser: {}", browser);

		logger.info("Headless mode: {}", headless);

		// ==================================================
		// CHROME
		// ==================================================

		if (browser.equalsIgnoreCase("chrome")) {

			WebDriverManager.chromedriver().avoidResolutionCache().setup();

			ChromeOptions options = new ChromeOptions();

			// Headless configuration
			if (headless) {

				options.addArguments("--headless=new");
				options.addArguments("--window-size=1920,1080");
				options.addArguments("--disable-gpu");

				logger.info("Running Chrome in HEADLESS mode");

			} else {

				logger.info("Running Chrome in NORMAL mode");
			}

			// Stability options
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--no-sandbox");
			options.addArguments("--remote-allow-origins=*");
			options.addArguments("--disable-notifications");

			driver.set(new ChromeDriver(options));

			logger.info("Chrome browser launched successfully");
		}

		// ==================================================
		// FIREFOX
		// ==================================================

		else if (browser.equalsIgnoreCase("firefox")) {

			WebDriverManager.firefoxdriver().avoidResolutionCache().setup();

			FirefoxOptions options = new FirefoxOptions();

			if (headless) {

				options.addArguments("--headless");

				logger.info("Running Firefox in HEADLESS mode");
			}

			driver.set(new FirefoxDriver(options));

			logger.info("Firefox browser launched successfully");
		}

		// ==================================================
		// EDGE
		// ==================================================

		else if (browser.equalsIgnoreCase("edge")) {
			
			WebDriverManager.edgedriver().clearDriverCache().clearResolutionCache().setup();

			//WebDriverManager.edgedriver().avoidResolutionCache().setup();

			EdgeOptions options = new EdgeOptions();

			if (headless) {

				options.addArguments("--headless=new");
				options.addArguments("--window-size=1920,1080");
				options.addArguments("--disable-gpu");

				logger.info("Running Edge in HEADLESS mode");
			}

			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--no-sandbox");

			driver.set(new EdgeDriver(options));

			logger.info("Edge browser launched successfully");
		}

		// ==================================================
		// INVALID BROWSER
		// ==================================================

		else {

			logger.error("Unsupported browser found in config file: {}", browser);

			throw new IllegalArgumentException("Browser not supported: " + browser);
		}

		// Register driver for Extent Reports
		ExtentManager.registerDriver(getDriver());
	}

	// ======================================================
	// BROWSER CONFIGURATION
	// ======================================================

	private void configBrowser() {

		try {

			// Implicit wait
			int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));

			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

			logger.info("Implicit wait set to {} seconds", implicitWait);

			// Maximize window only if NOT headless
			boolean headless = Boolean.parseBoolean(prop.getProperty("headless"));

			if (!headless) {

				getDriver().manage().window().maximize();

				logger.info("Browser window maximized");
			}

			// Launch application URL
			String url = prop.getProperty("url");

			getDriver().get(url);

			logger.info("Application launched successfully: {}", url);

		} catch (Exception e) {

			logger.error("Failed during browser configuration. Reason: {}", e.getMessage());

			throw e;
		}
	}

	// ======================================================
	// TEST CLEANUP
	// ======================================================

	@AfterMethod
	public synchronized void tearDown() {

		try {

			if (getDriver() != null) {

				logger.info("Closing browser instance");

				getDriver().quit();

				logger.info("Browser closed successfully");
			}

		} catch (Exception e) {

			logger.error("Unable to close browser. Reason: {}", e.getMessage());
		}

		finally {

			driver.remove();
			actionDriver.remove();

			logger.info("Driver ThreadLocal removed successfully");

			logger.info("========== TEST EXECUTION COMPLETED ==========");
		}
	}

	// ======================================================
	// GET DRIVER
	// ======================================================

	public static WebDriver getDriver() {

		if (driver.get() == null) {

			logger.error("WebDriver instance is NULL");

			throw new IllegalStateException("Driver is not initialized");
		}

		return driver.get();
	}

	// ======================================================
	// GET ACTION DRIVER
	// ======================================================

	public static ActionDriver getActionDriver() {

		if (actionDriver.get() == null) {

			logger.error("ActionDriver instance is NULL");

			throw new IllegalStateException("ActionDriver is not initialized");
		}

		return actionDriver.get();
	}

	// ======================================================
	// GET CONFIG PROPERTIES
	// ======================================================

	public static Properties getProp() {

		return prop;
	}

	// ======================================================
	// Waits
	// ======================================================

	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}