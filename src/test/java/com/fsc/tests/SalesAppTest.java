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
    private By accountName =By.xpath("//div[@class='entityNameTitle slds-line-height--reset']/following-sibling::slot/lightning-formatted-text");

    @BeforeMethod
    public void login(){
        // Initialize page objects and wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginPage = new SalesforceLoginPage(driver);
        salesAppPage = new SalesAppPage(driver);
        salesAppAccountPage = new SalesAppAccountPage(driver);

        // Login
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
    // @Test(priority = 2, description = "Test account creation")
    // public void testAccountCreation(){
        
    //     salesAppPage.navigateToSalesApp();
    //     salesAppAccountPage.navigateToAccountTab();
    //     salesAppAccountPage.createAccount();
        

    //     // Wait for success toast message
    //     // Toast element: <div class="slds-theme--success slds-notify--toast forceToastMessage">
    //     String createdAccountName = salesAppAccountPage.getCreatedAccountName();
    //     WebElement toastElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
    //         By.cssSelector("div.forceToastMessage")));
    //     String actualToastMessage = toastElement.getText();
    //     System.out.println("Toast message found: " + actualToastMessage);

    //     // Verify toast contains the account name (more flexible assertion)
    //     Assert.assertTrue(actualToastMessage.contains(createdAccountName),
    //         "Failed - Toast message does not contain account name. Expected to contain: " + createdAccountName + ". Actual message: " + actualToastMessage);

    //     // Verify you're on the Account detail page
    //     String currentUrl = driver.getCurrentUrl();
    //     Assert.assertTrue(currentUrl.contains("Account"),
    //         "Failed - URL does not contain 'Account'. Current URL: " + currentUrl);

    //     // Verify Account Name matches what you entered
    //     WebElement accountNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(accountName));
    //     String accountNameText = accountNameElement.getText();
        
    //     Assert.assertEquals(accountNameText, createdAccountName,
    //         "Failed - Account Name is not correct. Actual text: " + accountNameText);
    // }
    @Test(priority = 3, description = "Test account search")
    public void testAccountSearch(){
        
        
        salesAppPage.navigateToSalesApp();
        salesAppAccountPage.navigateToAccountTab();
        salesAppAccountPage.searchAccount();


        // Verify you're on the Account detail page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("Account"),
            "Failed - URL does not contain 'Account'. Current URL: " + currentUrl);

        // Verify Account Name contains Berardo
        WebElement accountNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(accountName));
        String accountNameText = accountNameElement.getText();
        
        Assert.assertEquals(accountNameText, "Berardo",
            "Failed - Account Name is not correct. Actual text: " + accountNameText);
    }
}
