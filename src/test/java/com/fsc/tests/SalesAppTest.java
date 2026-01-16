package com.fsc.tests;

import com.fsc.base.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fsc.pages.SalesforceLoginPage;
import com.fsc.utils.JavaScriptUtil;
import com.fsc.utils.ConfigReader;

import java.time.Duration;

public class SalesAppTest extends BaseTest{
    private SalesforceLoginPage loginPage;
    private WebDriverWait wait;

    // Locators - Multiple strategies for App Launcher
    private By appLauncherButton = By.xpath("//button[@title='App Launcher']");
    private By searchBar = By.xpath("//input[@placeholder='Search apps and items...']");
    private By salesAppLink = By.xpath("//a[@data-label='Sales']");
    private By appHeader = By.xpath("//h1[contains(@class, 'appName')]/span[@title='Sales']");

    @BeforeMethod
    public void login(){
        // Initialize wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Login
        loginPage = new SalesforceLoginPage(driver);
        loginPage.navigateToLogin(ConfigReader.getSalesforceUrl());
        String username = ConfigReader.getUsername();
        String password = ConfigReader.getPassword();
        loginPage.login(username, password);

        // Pause for MANUAL verification code entry
        System.out.println("=== MANUAL ACTION REQUIRED ===");
        System.out.println("Please enter the verification code from your email");
        System.out.println("You have 40 seconds...");
        try {
            Thread.sleep(40000);  // 40 seconds to enter code manually
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Continuing test...");

        // Wait for login to complete
        wait.until(driver -> {
            String url = driver.getCurrentUrl();
            return url.contains("lightning") || url.contains("home");
        });
    }

    @Test(priority = 1, description = "Test navigation to Sales app via App Launcher")
    public void testNavigateToSalesApp(){
        // Click App Launcher

        WebElement appLauncher = wait.until(ExpectedConditions.elementToBeClickable(appLauncherButton));
        appLauncher.click();

        // Wait for search bar and type "Sales"
        WebElement searchBarElement = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBar));
        searchBarElement.sendKeys("Sales");

        // Wait for Sales app to appear and click it
        WebElement salesApp = wait.until(ExpectedConditions.elementToBeClickable(salesAppLink));
        JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
        jsUtil.clickElement(salesApp);

        // Wait for navigation to complete
        wait.until(driver -> driver.getCurrentUrl().contains("lightning"));

        // Assert successful access to Sales app
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("lightning"),
            "Failed - URL does not contain 'lightning'. Current URL: " + currentUrl);

        // Verify header title attribute is "Sales"
        WebElement headerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(appHeader));
        String headerText = headerElement.getText();
        Assert.assertEquals(headerText, "Sales",
            "Failed - Header text is not 'Sales'. Actual text: " + headerText);
    }
}
