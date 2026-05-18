package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utils.ExtentManager;

public class HomePageTest extends BaseClass{
	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage  = new HomePage(getDriver()); 
	}
	
	@Test
	public void veryfyOrangeHRMLogo() {
		//ExtentManager.startTest("Login Test Home Test");-- This has been implemented in TestListner Class!
		ExtentManager.logSteps("Navigating to login page entering username and password");
		loginPage.login("admin", "admin123");
		ExtentManager.logSteps("Verify if the user is logged in or not");
		Assert.assertTrue(homePage.verifyOrangeHRMlogo(), " Logo is not visible");
		ExtentManager.logSteps("Validation successfully!");
		staticWait(5);
	}
}
