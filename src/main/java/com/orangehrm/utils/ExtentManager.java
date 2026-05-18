package com.orangehrm.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentManager class handles all reporting functionality for the automation
 * framework.
 *
 * Responsibilities: - Initialize Extent Report - Create and manage test entries
 * - Capture execution logs - Capture screenshots - Attach screenshots to
 * reports - Support parallel execution using ThreadLocal - Store browser
 * information for each execution thread
 *
 * ThreadLocal usage: Ensures test report objects remain isolated during
 * parallel execution.
 *
 * Framework: Selenium + TestNG + Extent Reports
 */
public class ExtentManager {

	// Main Extent report object
	private static ExtentReports extent;

	// Stores ExtentTest object per execution thread
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	// Stores WebDriver instance per thread
	private static Map<Long, WebDriver> driverMap = new HashMap<>();

	// Stores browser name per thread
	private static ThreadLocal<String> browserName = new ThreadLocal<>();

	/**
	 * Initialize Extent Report.
	 *
	 * Purpose: Creates report instance and applies configuration only once.
	 *
	 * Report location: /src/test/resources/extentreports
	 *
	 * Report configuration: - Report title - Theme - System information
	 *
	 * @return ExtentReports instance
	 */
	public synchronized static ExtentReports getReport() {

		if (extent == null) {

			// Report generation location
			String reportPath = System.getProperty("user.dir") + "/src/test/resources/extentreports/extentreport.html";

			// Initialize Spark reporter
			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

			// Report customization
			spark.config().setReportName("Automation Test Report");

			spark.config().setDocumentTitle("OrangeHRM");

			spark.config().setTheme(Theme.DARK);

			// Create report instance
			extent = new ExtentReports();

			extent.attachReporter(spark);

			// Add system information
			extent.setSystemInfo("Operating System", System.getProperty("os.name"));

			extent.setSystemInfo("Java Version", System.getProperty("java.version"));

			extent.setSystemInfo("User Name", System.getProperty("user.name"));

			/*
			 * Suggested log:
			 * 
			 * logger.info("Extent report initialized successfully");
			 */
		}

		return extent;
	}

	/**
	 * Create test instance inside report.
	 *
	 * Purpose: Creates a new test node and associates browser information.
	 *
	 * @param testName current executing test
	 * @return ExtentTest object
	 */
	public synchronized static ExtentTest startTest(String testName) {

		ExtentTest extentTest = getReport().createTest(testName);

		test.set(extentTest);

		// Add browser information
		extentTest.assignDevice(getBrowser());

		extentTest.info("Browser: " + getBrowser());

		/*
		 * Suggested log:
		 * 
		 * logger.info("Test started: {}", testName);
		 * logger.info("Running on browser: {}", getBrowser());
		 */

		return extentTest;
	}

	/**
	 * Flush report and save execution data.
	 *
	 * Purpose: Finalize report generation.
	 */
	public synchronized static void endTest() {

		getReport().flush();

		/*
		 * Suggested log:
		 * 
		 * logger.info("Extent report flushed successfully");
		 */
	}

	/**
	 * Get current thread ExtentTest object.
	 *
	 * @return current thread test object
	 */
	public synchronized static ExtentTest getTest() {
		return test.get();
	}

	/**
	 * Retrieve current test name.
	 *
	 * @return current test name
	 */
	public static String getTestName() {

		ExtentTest currentTest = getTest();

		if (currentTest != null) {

			return currentTest.getModel().getName();

		} else {

			return "No Test is currently active for this thread";
		}
	}

	/**
	 * Log informational step.
	 *
	 * @param logMessage step description
	 */
	public static void logSteps(String logMessage) {

		getTest().info(logMessage);

		/*
		 * Suggested log:
		 * 
		 * logger.info(logMessage);
		 */
	}

	/**
	 * Log successful step with screenshot.
	 *
	 * @param driver            active browser instance
	 * @param logMessage        success message
	 * @param screenShotMessage screenshot label
	 */
	public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenShotMessage) {

		getTest().pass(logMessage);

		attachScreenshot(driver, screenShotMessage);
	}

	/**
	 * Log failed step and attach screenshot.
	 *
	 * @param driver            browser instance
	 * @param logMessage        failure reason
	 * @param screenShotMessage screenshot label
	 */
	public static void logFailure(WebDriver driver, String logMessage, String screenShotMessage) {

		String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);

		getTest().fail(colorMessage);

		attachScreenshot(driver, screenShotMessage);

		/*
		 * Suggested log:
		 * 
		 * logger.error(logMessage);
		 */
	}

	/**
	 * Log skipped test details.
	 *
	 * @param logMessage skip reason
	 */
	public static void logSkip(String logMessage) {

		String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);

		getTest().skip(colorMessage);
	}

	/**
	 * Capture screenshot and save locally.
	 *
	 * Naming format: TestName_TimeStamp.png
	 *
	 * @param driver         browser instance
	 * @param screenshotname screenshot name
	 *
	 * @return Base64 image string
	 */
	public synchronized static String takeScreenshot(WebDriver driver, String screenshotname) {

		TakesScreenshot ts = (TakesScreenshot) driver;

		File src = ts.getScreenshotAs(OutputType.FILE);

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

		String destPath = System.getProperty("user.dir") + "/src/test/resources/screenshots/" + screenshotname + "_"
				+ timeStamp + ".png";

		File finalPath = new File(destPath);

		try {

			FileUtils.copyFile(src, finalPath);

		} catch (IOException e) {

			e.printStackTrace();

			/*
			 * Suggested log:
			 * 
			 * logger.error( "Screenshot capture failed: {}", e.getMessage());
			 */
		}

		return convertToBase64(src);
	}

	/**
	 * Convert screenshot to Base64.
	 *
	 * Purpose: Embed images directly inside reports.
	 *
	 * @param screenShotFile screenshot file
	 * @return Base64 string
	 */
	public static String convertToBase64(File screenShotFile) {

		String base64Format = "";

		try {

			byte[] fileContent = FileUtils.readFileToByteArray(screenShotFile);

			base64Format = Base64.getEncoder().encodeToString(fileContent);

		} catch (IOException e) {

			e.printStackTrace();
		}

		return base64Format;
	}

	/**
	 * Attach screenshot directly to report.
	 *
	 * @param driver  browser instance
	 * @param message report message
	 */
	public synchronized static void attachScreenshot(WebDriver driver, String message) {

		try {

			String screenShotBase64 = takeScreenshot(driver, getTestName());

			getTest().info(message,

					com.aventstack.extentreports.MediaEntityBuilder
							.createScreenCaptureFromBase64String(screenShotBase64).build());

		} catch (Exception e) {

			getTest().fail("Failed to attach screenshot:" + message);
		}
	}

	/**
	 * Register driver for current thread.
	 *
	 * Used for parallel execution support.
	 *
	 * @param driver current browser instance
	 */
	public static void registerDriver(WebDriver driver) {

		driverMap.put(Thread.currentThread().getId(), driver);
	}

	/**
	 * Store browser name for current thread.
	 *
	 * @param browser browser name
	 */
	public static void setBrowser(String browser) {

		browserName.set(browser);
	}

	/**
	 * Retrieve browser name for current thread.
	 *
	 * @return browser name
	 */
	public static String getBrowser() {

		return browserName.get();
	}
}