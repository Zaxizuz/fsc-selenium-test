package com.fsc.tests;

import com.fsc.base.BaseTest;
import com.fsc.pages.SalesforceLoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class SalesforceLoginTest extends BaseTest {
    private SalesforceLoginPage loginPage;
    private String salesforceUrl = "https://login.salesforce.com"; // Change to your Salesforce URL

    @BeforeMethod
    public void initializePage() {
        loginPage = new SalesforceLoginPage(driver);
        loginPage.navigateToLogin(salesforceUrl);
    }

    @Test(priority = 1, description = "Test successful login with valid credentials")
    @Parameters({"username", "password"})
    public void testValidLogin(String username, String password) {
        loginPage.login(username, password);

        // Add assertion for successful login
        // Example: Assert.assertTrue(driver.getCurrentUrl().contains("lightning"));
        // You'll need to customize this based on your Salesforce instance
    }

    @Test(priority = 2, description = "Test login with invalid credentials")
    public void testInvalidLogin() {
        loginPage.login("invalid@email.com", "wrongpassword");

        // Verify error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
            "Error message should be displayed for invalid credentials");
    }

    @Test(priority = 3, description = "Test login with empty username")
    public void testEmptyUsername() {
        loginPage.enterUsername("");
        loginPage.enterPassword("somepassword");
        loginPage.clickLoginButton();

        // Verify error message
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
            "Error message should be displayed for empty username");
    }

    @Test(priority = 4, description = "Test login with empty password")
    public void testEmptyPassword() {
        loginPage.enterUsername("test@email.com");
        loginPage.enterPassword("");
        loginPage.clickLoginButton();

        // Verify error message
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
            "Error message should be displayed for empty password");
    }
}
