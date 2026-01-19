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
import com.fsc.pages.SalesAppPage;
import com.fsc.pages.SalesAppAccountPage;
import com.fsc.utils.ConfigReader;

import java.time.Duration;

public class SalesAppTest extends BaseTest{
    private SalesforceLoginPage loginPage;
    private SalesAppPage salesAppPage;
    private SalesAppAccountPage salesAppAccountPage;
    private WebDriverWait wait;
    
    // Locators - Multiple strategies for App Launcher
    private By appHeader = By.xpath("//h1[contains(@class, 'appName')]/span[@title='Sales']");
    private By accountName =By.xpath("//slot[@name='primaryField']");

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

    // @Test(priority = 1, description = "Test navigation to Sales app via App Launcher")
    // public void testNavigateToSalesApp(){
        
    //     salesAppPage = new SalesAppPage(driver);
    //     salesAppPage.navigateToSalesApp();

    //     // Assert successful access to Sales app
    //     String currentUrl = driver.getCurrentUrl();
    //     Assert.assertTrue(currentUrl.contains("lightning"),
    //         "Failed - URL does not contain 'lightning'. Current URL: " + currentUrl);

    //     // Verify header title attribute is "Sales"
    //     WebElement headerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(appHeader));
    //     String headerText = headerElement.getText();
    //     Assert.assertEquals(headerText, "Sales",
    //         "Failed - Header text is not 'Sales'. Actual text: " + headerText);
    // }
    @Test(priority = 2, description = "Test account creation")
    public void testAccountCreation(){
        
        salesAppPage = new SalesAppPage(driver);
        salesAppPage.navigateToSalesApp();
        salesAppAccountPage = new SalesAppAccountPage(driver);
        salesAppAccountPage.createAccount();
        

        // Wait for success toat message
        String createdAccountName = salesAppAccountPage.getCreatedAccountName();
        String toastMessage = "Account "+ createdAccountName + " was created.";
        WebElement toastElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.slds-toast__content")));
        String actualToastMessage = toastElement.getText();
        Assert.assertEquals(actualToastMessage,toastMessage,
        "Failed - Toast event message is not correct. Actual message: " + actualToastMessage);

        // Verify you're on the Account detail page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("Account"),
            "Failed - URL does not contain 'Account'. Current URL: " + currentUrl);

        // Verify Account Name matches what you entered
        WebElement accountNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(accountName));
        String accountNameText = accountNameElement.getText();
        
        Assert.assertEquals(accountNameText, createdAccountName,
            "Failed - Account Name is not correct. Actual text: " + accountNameText);
    }
}
