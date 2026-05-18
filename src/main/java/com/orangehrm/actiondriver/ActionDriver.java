package com.orangehrm.actiondriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utils.ExtentManager;

public class ActionDriver {

	// ======================================================
	// GLOBAL VARIABLES
	// ======================================================

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger;

	// ======================================================
	// CONSTRUCTOR
	// ======================================================

	public ActionDriver(WebDriver driver) {

		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		logger.info("ActionDriver initialized successfully");
	}

	// ======================================================
	// CLICK ACTIONS
	// ======================================================

	/**
	 * Clicks on a web element after waiting for it to become clickable.
	 *
	 * @param by locator used to identify element
	 */
	public void click(By by) {

		String elementDescription = getElementDescription(by);

		try {

			waitForElementToBeClickable(by);
			applyBorder(by, "green");
			driver.findElement(by).click();
			ExtentManager.logSteps("Clicked on " + elementDescription);
			logger.info("Successfully clicked on {}", elementDescription);

		} catch (Exception e) {

			applyBorder(by, "red");
			ExtentManager.logFailure(BaseClass.getDriver(), "Failed to click on " + elementDescription,
					"Click failure screenshot");

			logger.error("Unable to click on {}. Reason: {}", elementDescription, e.getMessage());
		}
	}

	/**
	 * Clicks an element using JavaScript.
	 *
	 * @param by locator used to identify element
	 */
	public void clickUsingJS(By by) {

		String elementDescription = getElementDescription(by);

		try {
				WebElement element = driver.findElement(by);
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				applyBorder(by, "green");
				ExtentManager.logSteps("Clicked using JavaScript on " + elementDescription);
				logger.info("Clicked using JavaScript on {}", elementDescription);
		} catch (Exception e) {

			applyBorder(by, "red");
			logger.error("Unable to click using JavaScript on {}. Reason: {}", elementDescription, e.getMessage());
		}
	}

	// ======================================================
	// TEXT ACTIONS
	// ======================================================

	/**
	 * Enters text into an input field.
	 *
	 * @param by   locator
	 * @param text text to enter
	 */
	public void enterText(By by, String text) {

		String elementDescription = getElementDescription(by);

		try {

			waitForElelemntToBeVisible(by);
			WebElement element = driver.findElement(by);
			applyBorder(by, "green");
			element.clear();
			element.sendKeys(text);
			String highlightedText = "<span style='color:green;font-weight:bold;'>"  + text + "</span>";
			ExtentManager.logSteps("Entered text \""+highlightedText+"\" into " + elementDescription);
			logger.info("Entered text [{}] into {}", text, elementDescription);

		} catch (Exception e) {

			applyBorder(by, "red");
			String highlightedText = "<span style='color:red;font-weight:bold;'>"  + text + "</span>";
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to enter text \""+highlightedText+"\"  into " + elementDescription,
					"Text entry failure screenshot");

			logger.error("Unable to enter text into {}. Reason: {}", elementDescription, e.getMessage());
		}
	}

	/**
	 * Returns text from an element.
	 *
	 * @param by locator
	 * @return element text
	 */
	public String getText(By by) {

		String elementDescription = getElementDescription(by);

		try {

			waitForElelemntToBeVisible(by);
			String text = driver.findElement(by).getText();
			logger.info("Retrieved text from {}", elementDescription);
			return text;

		} catch (Exception e) {

			logger.error("Unable to retrieve text from {}. Reason: {}", elementDescription, e.getMessage());
			return "";
		}
	}

	/**
	 * Compares actual and expected text.
	 *
	 * @param by           locator
	 * @param expectedText expected value
	 * @return true if matched
	 */
	public boolean compareText(By by, String expectedText) {

		String elementDescription = getElementDescription(by);

		try {

			waitForElelemntToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				
				applyBorder(by, "green");
				String highlightedexpectedText = "<span style='color:green;font-weight:bold;'>"  + expectedText + "</span>";
				String highlightedactualText = "<span style='color:green;font-weight:bold;'>"  + actualText + "</span>";
				
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Text validation passed "+highlightedexpectedText+" <--> "+highlightedactualText,
						"Validation screenshot");
				logger.info("Validation PASSED for {}", elementDescription);
				return true;

			} else {

				applyBorder(by, "red");
				ExtentManager.logFailure(BaseClass.getDriver(), "Text validation failed",
						"Expected: " + expectedText + " | Actual: " + actualText);
				logger.error("Validation FAILED for {}", elementDescription);
				return false;
			}

		} catch (Exception e) {
			logger.error("Unable to compare text. Reason: {}", e.getMessage());
			return false;
		}
	}

	// ======================================================
	// ELEMENT VALIDATION
	// ======================================================

	/**
	 * Checks whether an element is displayed.
	 *
	 * @param by locator
	 * @return true if displayed
	 */
	public boolean isDisplayed(By by) {

		String elementDescription = getElementDescription(by);

		try {

			waitForElelemntToBeVisible(by);
			boolean displayed = driver.findElement(by).isDisplayed();
			if (displayed) {
				applyBorder(by, "green");
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element displayed successfully",
						"Element visibility screenshot");
				logger.info("Element displayed: {}", elementDescription);
				return true;
			}

		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Element not displayed {}. Reason: {}", elementDescription, e.getMessage());
		}
		return false;
	}

	// ======================================================
	// WAIT METHODS
	// ======================================================

	public void waitForElementToBeClickable(By by) {

		try {

			logger.info("Waiting for element to become clickable: {}", by);
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element not clickable: {}. Reason: {}", by, e.getMessage());
		}
	}

	public void waitForElelemntToBeVisible(By by) {

		try {
			logger.info("Waiting for element visibility: {}", by);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element not visible: {}. Reason: {}", by, e.getMessage());
		}
	}

	public void waitForPageLoad(int timeoutInSeconds) {

		try {
			wait.withTimeout(Duration.ofSeconds(timeoutInSeconds)).until(webDriver -> ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState").equals("complete"));
			logger.info("Page loaded successfully");
		} catch (Exception e) {

			logger.error("Page did not load within {} seconds. Reason: {}", timeoutInSeconds, e.getMessage());
		}
	}

	// ======================================================
	// SCROLL METHODS
	// ======================================================

	public void scrollToElement(By by) {

		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			applyBorder(by, "green");
			logger.info("Scrolled to element {}", getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to scroll to element. Reason: {}", e.getMessage());
		}
	}

	public void scrollToBottom() {

		try {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
			logger.info("Scrolled to bottom of page");
		} catch (Exception e) {
			logger.error("Unable to scroll to bottom. Reason: {}", e.getMessage());
		}
	}

	// ======================================================
	// DROPDOWN METHODS
	// ======================================================

	public void selectByVisibleText(By by, String visibleText) {

		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByVisibleText(visibleText);
			applyBorder(by, "green");
			logger.info("Selected dropdown value [{}]", visibleText);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown value [{}]. Reason: {}", visibleText, e.getMessage());
		}
	}

	public void selectByValue(By by, String value) {

		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByValue(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown by value [{}]", value);
		} catch (Exception e) {
			logger.error("Unable to select dropdown value [{}]. Reason: {}", value, e.getMessage());
		}
	}

	public void selectByIndex(By by, int index) {

		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByIndex(index);
			applyBorder(by, "green");
			logger.info("Selected dropdown index [{}]", index);
		} catch (Exception e) {
			logger.error("Unable to select dropdown index [{}]. Reason: {}", index, e.getMessage());
		}
	}

	public List<String> getDropdownOptions(By by) {
		List<String> options = new ArrayList<>();

		try {
			WebElement dropdown = driver.findElement(by);
			Select select = new Select(dropdown);
			for (WebElement option : select.getOptions()) {
				options.add(option.getText());
			}
			logger.info("Retrieved dropdown options");
		} catch (Exception e) {
			logger.error("Unable to retrieve dropdown options. Reason: {}", e.getMessage());
		}
		return options;
	}

	// ======================================================
	// WINDOW HANDLING
	// ======================================================

	public void switchToWindow(String windowTitle) {

		try {

			Set<String> windows = driver.getWindowHandles();
			for (String window : windows) {
				driver.switchTo().window(window);
				if (driver.getTitle().equals(windowTitle)) {
					logger.info("Switched to window: {}", windowTitle);
					return;
				}
			}
			logger.warn("No window found with title: {}", windowTitle);
		} catch (Exception e) {
			logger.error("Unable to switch window. Reason: {}", e.getMessage());
		}
	}

	// ======================================================
	// FRAME HANDLING
	// ======================================================

	public void switchToFrame(By by) {

		try {
			driver.switchTo().frame(driver.findElement(by));
			logger.info("Switched to iframe: {}", getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to switch to iframe. Reason: {}", e.getMessage());
		}
	}

	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
		logger.info("Switched back to default content");
	}

	// ======================================================
	// ALERT HANDLING
	// ======================================================

	public void acceptAlert() {

		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			logger.info("Alert accepted");
		} catch (Exception e) {
			logger.error("Unable to accept alert. Reason: {}", e.getMessage());
		}
	}

	public void dismissAlert() {

		try {
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
			logger.info("Alert dismissed");
		} catch (Exception e) {
			logger.error("Unable to dismiss alert. Reason: {}", e.getMessage());
		}
	}

	public String getAlertText() {

		try {
			Alert alert = driver.switchTo().alert();
			return alert.getText();
		} catch (Exception e) {
			logger.error("Unable to retrieve alert text. Reason: {}", e.getMessage());
			return "";
		}
	}

	// ======================================================
	// BROWSER UTILITIES
	// ======================================================

	public void refreshPage() {

		try {
			driver.navigate().refresh();
			ExtentManager.logSteps("Page refreshed successfully");
			logger.info("Page refreshed successfully");

		} catch (Exception e) {
			logger.error("Unable to refresh page. Reason: {}", e.getMessage());
		}
	}

	public String getCurrentURL() {

		try {

			String url = driver.getCurrentUrl();
			logger.info("Current URL: {}", url);
			return url;

		} catch (Exception e) {
			logger.error("Unable to retrieve current URL. Reason: {}", e.getMessage());
			return null;
		}
	}

	public void maximizeWindow() {

		try {

			driver.manage().window().maximize();
			logger.info("Browser window maximized");
		} catch (Exception e) {
			logger.error("Unable to maximize browser window. Reason: {}", e.getMessage());
		}
	}

	// ======================================================
	// UTILITY METHODS
	// ======================================================

	public void applyBorder(By by, String color) {

		try {
			WebElement element = driver.findElement(by);
			String script = "arguments[0].style.border='3px solid " + color + "'";
			((JavascriptExecutor) driver).executeScript(script, element);
		} catch (Exception e) {
			logger.warn("Unable to apply border. Reason: {}", e.getMessage());
		}
	}

	public String getElementDescription(By locator) {

		if (locator == null) {
			return "Unknown Element";
		}

		try {

			WebElement element = driver.findElement(locator);

			String name = element.getDomAttribute("name");

			String id = element.getDomAttribute("id");

			String text = element.getText();

			String placeholder = element.getDomAttribute("placeholder");

			if (isNotEmpty(name)) {
				return "Element [name=" + name + "]";
			}

			if (isNotEmpty(id)) {
				return "Element [id=" + id + "]";
			}

			if (isNotEmpty(text)) {
				return "Element [text=" + truncate(text, 30) + "]";
			}

			if (isNotEmpty(placeholder)) {
				return "Element [placeholder=" + placeholder + "]";
			}

		} catch (Exception e) {

			logger.error("Unable to describe element. Reason: {}", e.getMessage());
		}

		return "Unknown Element";
	}

	private boolean isNotEmpty(String value) {

		return value != null && !value.isEmpty();
	}

	private String truncate(String value, int maxLength) {

		if (value == null || value.length() <= maxLength) {

			return value;
		}

		return value.substring(0, maxLength) + "...";
	}
}