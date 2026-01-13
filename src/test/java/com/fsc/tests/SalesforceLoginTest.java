package com.fsc.tests;

import com.fsc.base.BaseTest;
import com.fsc.pages.SalesforceLoginPage;
import com.fsc.utils.ConfigReader;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class SalesforceLoginTest extends BaseTest {
    private SalesforceLoginPage loginPage;

    @BeforeMethod
    public void initializePage() {
        loginPage = new SalesforceLoginPage(driver);
        loginPage.navigateToLogin(ConfigReader.getSalesforceUrl());
    }

    @Test(priority = 1, description = "Test successful login with valid credentials")
    public void testValidLogin() {
        String username = ConfigReader.getUsername();
        String password = ConfigReader.getPassword();

        loginPage.login(username, password);

        // Wait for successful login - URL should change to contain 'lightning' or 'home'
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        wait.until(driver -> {
            String url = driver.getCurrentUrl();
            return url.contains("lightning") || url.contains("home");
        });

        // Assert successful login
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("lightning") || currentUrl.contains("home"),
            "Login failed - URL does not contain 'lightning' or 'home'. Current URL: " + currentUrl);
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
