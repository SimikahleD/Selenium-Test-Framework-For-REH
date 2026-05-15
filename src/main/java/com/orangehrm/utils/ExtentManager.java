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

public class ExtentManager {

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static Map<Long, WebDriver> driverMap = new HashMap<>();

	// Initialize Extent Report//src\test\resources\ExtentReports
	public synchronized static ExtentReports getReport() {
		if (extent == null) {
			String reportPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReports/ExtentReport.html";
			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

			spark.config().setReportName("Automationn Test Report");
			spark.config().setDocumentTitle("OrangeHRM");
			spark.config().setTheme(Theme.DARK);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			// Adding System INFO
			extent.setSystemInfo("Operating System ", System.getProperty("os.name"));
			extent.setSystemInfo("Java Version ", System.getProperty("java.version"));
			extent.setSystemInfo("User Name ", System.getProperty("user.name"));
		}
		return extent;
	}

	// Start the test
	public synchronized static ExtentTest startTest(String testName) {
		ExtentTest extentTest = getReport().createTest(testName);
		test.set(extentTest);
		return extentTest;
	}

	// End Test
	public synchronized static void endTest() {
		getReport().flush();
	}

	// Get Current Thread Test name
	public synchronized static ExtentTest getTest() {
		return test.get();
	}

	// Method to get the name of the Current Test
	public static String getTestName() {
		ExtentTest currentTest = getTest();
		if (currentTest != null) {
			return currentTest.getModel().getName();
		} else {
			return "No Test is currently active for this thread";
		}
	}

	// Log Steps
	public static void logSteps(String logMessage) {
		getTest().info(logMessage);
	}

	// Log a step validation with screenshot
	public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenShotMessage) {
		getTest().pass(logMessage);
		// Screenshot method
		attachScreenshot(driver,screenShotMessage);
	}

	// Log a Failure
	public static void logFailure(WebDriver driver, String logMessage, String screenShotMessage) {
		String colorMessage = String.format("<span style='color:red;'>%s</span>",logMessage);
		getTest().fail(colorMessage);
		// Screenshot method
		attachScreenshot(driver,screenShotMessage);
	}

	// Log a skip
	public static void logSkip(String logMessage) {
		String colorMessage = String.format("<span style='color:orange;'>%s</span>",logMessage);
		getTest().skip(colorMessage);
	}

	// Take a screenshot with date and time in the file
	public synchronized static String takeScreenshot(WebDriver driver, String screenshotName) {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File src = ts.getScreenshotAs(OutputType.FILE);
		// Format date and Time for file name
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

		// Saving the screenshot to a file
		String destPath = System.getProperty("user.dir") + "/src/test/resources/Screenshots/" + screenshotName + "_"
				+ timeStamp + ".png";

		File finalPath = new File(destPath);
		try {
			FileUtils.copyFile(src, finalPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Convert screenshot to Base64 for embedding in the Report
		String base64Format = convertToBase64(src);
		return base64Format;
	}

	// Convert screenshot to Base64 format
	public static String convertToBase64(File screenShotFile) {
		String base64Format = "";
		// Read the file content into a byte array
		try {
			byte[] fileContent = FileUtils.readFileToByteArray(screenShotFile);
			base64Format = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return base64Format;
	}

	// Attach screenshot to report using Base64
	public synchronized static void attachScreenshot(WebDriver driver, String message) {
		try {
			String screenShotBase64 = takeScreenshot(driver, getTestName());
			getTest().info(message, com.aventstack.extentreports.MediaEntityBuilder
					.createScreenCaptureFromBase64String(screenShotBase64).build());
		} catch (Exception e) {
			getTest().fail("Failed to attach screenshot:" + message);
			e.printStackTrace();
		}
	}

	// Register WebDriver for current Thread
	public static void registerDriver(WebDriver driver) {
		driverMap.put(Thread.currentThread().getId(), driver);
	}
}
