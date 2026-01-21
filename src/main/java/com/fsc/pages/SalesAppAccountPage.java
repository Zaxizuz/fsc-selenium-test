package com.fsc.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;

import com.fsc.utils.JavaScriptUtil;


import java.time.Duration;


public class SalesAppAccountPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavaScriptUtil jsUtil;
    private Actions actionsUtil;

    // Locators
    private By accountsTab = By.xpath("//a[@title='Accounts']");
    private By newButton = By.xpath("//a[@title='New']");
    private By businessRadioButton = By.xpath("(//span[@class='slds-radio--faux'])[3]");
    private By nextButton =By.xpath("//button[@class='slds-button slds-button_neutral slds-button slds-button_brand uiButton']");
    private By nameField=By.xpath("//input[@name='Name']");
    private By typeField=By.xpath("(//button[@class='slds-combobox__input slds-input_faux fix-slds-input_faux slds-combobox__input-value'])[2]");
    private By customerDirectOption=By.xpath("//lightning-base-combobox-item[@data-value='Customer - Direct']");
    private By industryField=By.xpath("(//button[@class='slds-combobox__input slds-input_faux fix-slds-input_faux slds-combobox__input-value'])[3]");
    private By technologyOption=By.xpath("//lightning-base-combobox-item[@data-value='Technology']");
    private By phoneField=By.xpath("//input[@name='Phone']");
    private By saveButton=By.xpath("//button[@name='SaveEdit']");
    private By searchBar=By.xpath("//input[@name='Account-search-input']");
    private By listViewButton=By.xpath("//button[@title='Select a List View: Accounts']");
    private By allAccountOption=By.xpath("//*[text()='All Accounts']");
    private By firstRecord=By.xpath("//span[@data-cell-type='lstOutputLookup'][1]//a");
    private String createdAccountName;

    public SalesAppAccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        jsUtil = new JavaScriptUtil(driver);
        actionsUtil = new Actions(driver);
    }

    public void navigateToAccountTab(){
        // Click Accounts tab
        WebElement accountsTabelement = wait.until(ExpectedConditions.elementToBeClickable(accountsTab));
        jsUtil.clickElement(accountsTabelement);
    }
    public void searchAccount(){
        // Select "All Account" List view
        WebElement accountsTabelement = wait.until(ExpectedConditions.elementToBeClickable(listViewButton));
        accountsTabelement.click();
        WebElement allAccountElement = wait.until(ExpectedConditions.elementToBeClickable(allAccountOption));
        jsUtil.clickElement(allAccountElement);
        
        // Enter the account name in the search bar
        WebElement searchBarElement = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBar));
        searchBarElement.sendKeys("Berardo" + Keys.ENTER);

        // Wait for spinner to disappear before clicking
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("lightning-spinner")));

        // Go to the first record - use JavaScript click to avoid spinner interference
        WebElement firstRecordLink = wait.until(ExpectedConditions.elementToBeClickable(firstRecord));
        jsUtil.clickElement(firstRecordLink);
    
    }

    public void createAccount() {

        // Click "New" button
        WebElement newButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(newButton));
        newButtonElement.click();

        // Select Business Account Type
        WebElement businessRadioButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(businessRadioButton));
        businessRadioButtonElement.click();
        WebElement nextButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(nextButton));
        nextButtonElement.click();
        // Fill in Account Name
        WebElement nameFieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
        createdAccountName = "Test Account " + System.currentTimeMillis();
        nameFieldElement.sendKeys(createdAccountName);

        // Select Account Type
        WebElement typeFieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(typeField));
        typeFieldElement.click();

        WebElement customerDirectElement = wait.until(ExpectedConditions.elementToBeClickable(customerDirectOption));
        jsUtil.clickElement(customerDirectElement);

        // Select Industry
        WebElement industryElement = wait.until(ExpectedConditions.elementToBeClickable(industryField));
        industryElement.click();

        WebElement technologyElement = wait.until(ExpectedConditions.elementToBeClickable(technologyOption));
        jsUtil.clickElement(technologyElement);

        // Fill in Phone
        WebElement phoneFieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated(phoneField));
        phoneFieldElement.sendKeys("0412345678");

        // Click "Save button"
        WebElement saveButtonElement = wait.until(ExpectedConditions.visibilityOfElementLocated(saveButton));
        saveButtonElement.click();
    }

    public String getCreatedAccountName(){
        return createdAccountName;
    }
}
