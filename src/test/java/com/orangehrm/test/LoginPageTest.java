package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utils.ExtentManager;

public class LoginPageTest extends BaseClass{
	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage  = new HomePage(getDriver());
	}
	
	@Test
	public void veryfyLoginTest() {
		//ExtentManager.startTest("Login Test"); -- This has been implemented in TestListner Class!
		ExtentManager.logSteps("Navigated to OrangeHRM login page");
		ExtentManager.logSteps("Entered username and password into login form");
		loginPage.login("admin", "admin123");
		ExtentManager.logSteps("Waiting for dashboard page to load");
		ExtentManager.logSteps("Verify if the user is logged in or not");
		Assert.assertTrue(homePage.isAdminTabVisible(), " Admin tab should be visible after successfull login");
		ExtentManager.logSteps("Validation successfully!");
		homePage.logout();
		ExtentManager.logSteps("Logged out successfully!");
		staticWait(5);
	}
	
	@Test
	public void inValidLogin() {
		//ExtentManager.startTest("Invalid Login Test");-- This has been implemented in TestListner Class!
		ExtentManager.logSteps("Navigating to login page entering username and password");
		loginPage.login("admin", "admin23");
		ExtentManager.logSteps("Verify if the user is logged in or not");
		String expectedErrorMessage = "Invalid credentials";
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage), " Test Failed invalid error message");
		ExtentManager.logStepWithScreenshot(getDriver(), "User not logged in ", " Expected Error Message! "+expectedErrorMessage);
	}
}
