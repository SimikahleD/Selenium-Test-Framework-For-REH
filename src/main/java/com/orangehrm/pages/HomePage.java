package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

/**
 * HomePage class represents the OrangeHRM Home/Dashboard page.
 * 
 * Purpose: - Store page locators - Perform actions on Home page components -
 * Follow Page Object Model (POM) design principles - Keep UI logic separate
 * from test classes
 * 
 * Features covered: - Verify Admin tab visibility - Verify OrangeHRM logo -
 * Navigate to PIM module - Search employee - Logout functionality
 */
public class HomePage {

	// ActionDriver object used for reusable Selenium actions
	private ActionDriver actionDriver;

	/*
	 * ============================== Page Locators ==============================
	 */

	// Admin module tab
	private By adminTab = By.xpath("//span[text()='Admin']");

	// User profile dropdown displayed top-right
	private By userIDButton = By.className("oxd-userdropdown-name");

	// Logout option inside user dropdown
	private By logoutButton = By.xpath("//a[text()='Logout']");

	// OrangeHRM application logo
	private By oranageHRMlogo = By.xpath("//div[@class='oxd-brand-banner']//img");

	// PIM module tab
	private By pimTab = By.xpath("//span[text()='PIM']");

	// Employee name search textbox
	private By employeeSearch = By
			.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div/div/div/input");

	// Search button
	private By searchButton = By.xpath("//button[@type='submit']");

	// Employee first and middle name column
	private By emplFirstAndMiddleName = By.xpath("//div[@class='oxd-table-card']/div/div[3]");

	// Employee last name column
	private By emplLastName = By.xpath("//div[@class='oxd-table-card']/div/div[4]");

	/**
	 * Constructor for HomePage
	 * 
	 * Initializes ActionDriver instance using BaseClass. Allows page methods to
	 * access reusable browser actions.
	 *
	 * @param driver WebDriver instance
	 */
	public HomePage(WebDriver driver) {
		this.actionDriver = BaseClass.getActionDriver();
	}

	/**
	 * Verify whether Admin tab is displayed.
	 *
	 * Purpose: Used to validate successful login and page visibility.
	 *
	 * @return true if visible false otherwise
	 */
	public boolean isAdminTabVisible() {
		return actionDriver.isDisplayed(adminTab);
	}

	/**
	 * Verify OrangeHRM logo visibility.
	 *
	 * Purpose: Validate Home page branding element.
	 *
	 * @return true if logo exists false otherwise
	 */
	public boolean verifyOrangeHRMlogo() {
		return actionDriver.isDisplayed(oranageHRMlogo);
	}

	/**
	 * Navigate to PIM module.
	 *
	 * Purpose: Allows access to employee management functionality.
	 */
	public void clickOnPIMTab() {
		actionDriver.click(pimTab);
	}

	/**
	 * Search for employee using employee name.
	 *
	 * Flow: 1. Enter employee name 2. Click Search button 3. Scroll to search
	 * result section
	 *
	 * @param value Employee name to search
	 */
	public void employeeSearch(String value) {

		// Enter employee name
		actionDriver.enterText(employeeSearch, value);

		// Click Search button
		actionDriver.click(searchButton);

		// Scroll to employee results
		actionDriver.scrollToElement(emplFirstAndMiddleName);
	}

	/**
	 * Perform logout action.
	 *
	 * Flow: 1. Open user dropdown 2. Click Logout
	 *
	 * Purpose: Ends current user session.
	 */
	public void logout() {

		// Open user profile menu
		actionDriver.click(userIDButton);

		// Click logout option
		actionDriver.click(logoutButton);
	}
}