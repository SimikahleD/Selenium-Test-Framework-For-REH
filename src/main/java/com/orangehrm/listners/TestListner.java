package com.orangehrm.listners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utils.ExtentManager;

public class TestListner implements ITestListener{
	
	//Trigger when a test starts
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		//Start logging extent report
		ExtentManager.startTest(testName);
		ExtentManager.logSteps("Test started: "+testName);
	}
	
	//Triggered when a test pass
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		//Start logging a passed test
		ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test passes Successfully", "Test End: " + testName + " - ✔ Test Passed");
		
	}

	//Triggered when a test fails
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		//Start logging a failed test
		String failureMessage = result.getThrowable().getMessage();
		ExtentManager.logSteps(failureMessage);
		ExtentManager.logFailure(BaseClass.getDriver(), "Test did not run Successfully", "Test End: " + testName + " - ❌ Test Failed" );
	}

	//Trigger when a test skips
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		//Start logging when a test skip
		ExtentManager.logSkip("Test Skipped: "+testName);
	}
	
	//This will trigger when a Suite Start
	@Override
	public void onStart(ITestContext context) {
		//Initializing Extent Report
		ExtentManager.getReport();
	}

	//Triggered when a Suite ends
	@Override
	public void onFinish(ITestContext context) {
		//Flush the report
		ExtentManager.endTest();
	}
}
