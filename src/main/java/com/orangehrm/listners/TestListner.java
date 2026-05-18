package com.orangehrm.listners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utils.ExtentManager;

/**
 * TestListener class implements TestNG's ITestListener interface.
 * 
 * Purpose: - Monitor test execution lifecycle events - Integrate Extent Reports
 * logging - Capture screenshots on pass/failure - Record test status and
 * execution details
 * 
 * This listener automatically reacts whenever: - A test starts - A test passes
 * - A test fails - A test is skipped - Test suite execution starts/ends
 */
public class TestListner implements ITestListener {

	/**
	 * Triggered before each test method execution.
	 * 
	 * Purpose: - Creates a new test entry in Extent Report - Logs test start
	 * information
	 */
	@Override
	public void onTestStart(ITestResult result) {

		// Retrieve current test method name
		String testName = result.getMethod().getMethodName();

		// Create new test instance in Extent Report
		ExtentManager.startTest(testName);

		// Log test start event
		ExtentManager.logSteps("Test started: " + testName);
	}

	/**
	 * Triggered when test executes successfully.
	 * 
	 * Purpose: - Log successful execution - Capture screenshot for passed tests -
	 * Update Extent Report status
	 */
	@Override
	public void onTestSuccess(ITestResult result) {

		// Get executed test name
		String testName = result.getMethod().getMethodName();

		// Log pass status and attach screenshot
		ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test passes Successfully",
				"Test End: " + testName + " - ✔ Test Passed");
	}

	/**
	 * Triggered when a test execution fails.
	 * 
	 * Purpose: - Capture failure details - Log exception/error message - Take
	 * screenshot automatically - Mark test as failed in Extent Report
	 */
	@Override
	public void onTestFailure(ITestResult result) {

		// Retrieve failed test name
		String testName = result.getMethod().getMethodName();

		// Capture actual exception message
		String failureMessage = result.getThrowable().getMessage();

		// Log exception details
		ExtentManager.logSteps(failureMessage);

		// Capture screenshot and log failure
		ExtentManager.logFailure(BaseClass.getDriver(), "Test did not run Successfully",
				"Test End: " + testName + " - ❌ Test Failed");
	}

	/**
	 * Triggered when a test is skipped.
	 * 
	 * Possible reasons: - Dependency failure - Conditional skip - Configuration
	 * issues
	 * 
	 * Purpose: - Log skipped test details
	 */
	@Override
	public void onTestSkipped(ITestResult result) {

		// Retrieve skipped test name
		String testName = result.getMethod().getMethodName();

		// Log skip event
		ExtentManager.logSkip("Test Skipped: " + testName);
	}

	/**
	 * Triggered once before suite execution begins.
	 * 
	 * Purpose: - Initialize Extent Report - Create report instance
	 */
	@Override
	public void onStart(ITestContext context) {

		// Initialize report setup
		ExtentManager.getReport();
	}

	/**
	 * Triggered after all test execution completes.
	 * 
	 * Purpose: - Flush and save report data - Finalize report generation
	 */
	@Override
	public void onFinish(ITestContext context) {

		// Write all logs into final report
		ExtentManager.endTest();
	}
}