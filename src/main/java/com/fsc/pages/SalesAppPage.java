package com.fsc.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fsc.utils.JavaScriptUtil;

import java.time.Duration;


public class SalesAppPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    // Locators - Multiple strategies for App Launcher
    private By appLauncherButton = By.xpath("//button[@title='App Launcher']");
    private By searchBar = By.xpath("//input[@placeholder='Search apps and items...']");
    private By salesAppLink = By.xpath("//a[@data-label='Sales']");

    public SalesAppPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToSalesApp() {
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
    }
}
