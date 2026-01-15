# JavascriptExecutor in Salesforce Testing

## Why JavascriptExecutor is Essential for Salesforce

Salesforce Lightning uses modern web technologies that often require JavaScript execution:
- **Shadow DOM** - Hides elements from standard Selenium
- **Dynamic overlays** - Spinners, toasts, modals block clicks
- **Custom web components** - Lightning components need special handling
- **Lazy loading** - Content loads on scroll

---

## Common Salesforce Scenarios

### 1. Click Element Blocked by Overlay (Most Common!)

**Problem**: Lightning spinner or toast blocks the element

```java
// ❌ FAILS: ElementClickInterceptedException
driver.findElement(By.xpath("//button[@title='New']")).click();

// ✅ WORKS: JavaScript click bypasses overlay
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement newButton = driver.findElement(By.xpath("//button[@title='New']"));
jsUtil.clickElement(newButton);
```

---

### 2. Access Shadow DOM Elements

**Problem**: Lightning input fields hidden in Shadow DOM

```java
// Lightning input structure:
// <lightning-input>
//   #shadow-root
//     <input>  ← Can't access with regular Selenium

// ✅ Solution:
WebElement lightningInput = driver.findElement(By.cssSelector("lightning-input"));
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement shadowRoot = jsUtil.getShadowRoot(lightningInput);
WebElement input = shadowRoot.findElement(By.cssSelector("input"));
input.sendKeys("John Doe");
```

---

### 3. Scroll to Element (Lazy Loading)

**Problem**: Element not visible until scrolled into view

```java
// ✅ Scroll element into view
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement element = driver.findElement(By.xpath("//a[text()='Opportunities']"));
jsUtil.scrollIntoView(element);

// Small wait for scroll animation
Thread.sleep(300);

// Now click
element.click();
```

**Infinite scroll**:
```java
// Scroll to bottom to load more records
jsUtil.scrollToBottom();
wait.until(driver ->
    driver.findElements(By.cssSelector(".slds-table tbody tr")).size() > 10
);
```

---

### 4. Set Value and Trigger Events

**Problem**: Lightning components need change events

```java
// ❌ sendKeys() doesn't trigger Lightning events properly
input.sendKeys("value");

// ✅ Set value and trigger change event
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement input = driver.findElement(By.cssSelector("input[name='firstName']"));
jsUtil.setValueAndTriggerChange(input, "John");
```

---

### 5. Wait for Lightning Spinner

**Problem**: Need to wait for loading to complete

```java
public void waitForSpinnerToDisappear() {
    JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    wait.until(driver -> !jsUtil.isSpinnerPresent());
}

// Use in test:
loginPage.login("user@example.com", "password");
waitForSpinnerToDisappear();  // Wait for login to complete
```

---

### 6. Handle Lightning Combobox (Picklist)

**Problem**: Standard select methods don't work with Lightning combobox

```java
public void selectFromCombobox(String fieldLabel, String value) {
    JavaScriptUtil jsUtil = new JavaScriptUtil(driver);

    // Find and click combobox
    WebElement combobox = driver.findElement(
        By.xpath("//label[text()='" + fieldLabel + "']//ancestor::lightning-combobox")
    );
    jsUtil.clickElement(combobox);

    // Wait for dropdown
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//span[@title='" + value + "']")
    ));

    // Click option
    WebElement option = driver.findElement(By.xpath("//span[@title='" + value + "']"));
    jsUtil.clickElement(option);
}

// Usage:
selectFromCombobox("Status", "In Progress");
```

---

### 7. Debugging - Highlight Elements

**Problem**: Hard to see which element Selenium is interacting with

```java
// Highlight element for debugging
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement element = driver.findElement(By.id("accountName"));

jsUtil.highlightElement(element);  // Red border appears
element.sendKeys("Acme Corp");
jsUtil.removeHighlight(element);   // Remove border

// Or flash element 3 times
jsUtil.flashElement(element);
```

---

### 8. Remove Read-Only Attributes

**Problem**: Some fields are readonly in certain contexts

```java
// Remove readonly to enable editing
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement field = driver.findElement(By.id("lockedField"));
jsUtil.removeReadOnly(field);
field.clear();
field.sendKeys("New value");
```

---

### 9. Check True Visibility

**Problem**: Element exists but has zero dimensions

```java
// Selenium isDisplayed() can lie for some Lightning components
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement element = driver.findElement(By.className("hidden-component"));

// Check if really visible (has dimensions)
if (jsUtil.isElementVisible(element)) {
    element.click();
}
```

---

### 10. Handle Lightning Modals

**Problem**: Modal backdrop blocks clicks

```java
public void closeModal() {
    JavaScriptUtil jsUtil = new JavaScriptUtil(driver);

    // Wait for modal
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector(".slds-modal")
    ));

    // Close button might be blocked by backdrop
    WebElement closeBtn = driver.findElement(
        By.xpath("//button[@title='Close']")
    );

    jsUtil.clickElement(closeBtn);  // JavaScript click bypasses backdrop

    // Wait for modal to close
    wait.until(ExpectedConditions.invisibilityOfElementLocated(
        By.cssSelector(".slds-modal")
    ));
}
```

---

## Real Test Example

```java
package com.fsc.tests;

import com.fsc.base.BaseTest;
import com.fsc.utils.ConfigReader;
import com.fsc.utils.JavaScriptUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class SalesforceAccountCreationTest extends BaseTest {

    @Test
    public void testCreateNewAccount() {
        JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Login
        driver.get(ConfigReader.getSalesforceUrl());
        // ... login code ...

        // Click New button (might be blocked by loading spinner)
        WebElement newButton = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[@title='New']")
            )
        );

        // Use JS click to avoid spinner blocking
        jsUtil.clickElement(newButton);

        // Wait for modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".slds-modal")
        ));

        // Fill account name (might be in shadow DOM or need events)
        WebElement accountNameInput = driver.findElement(
            By.xpath("//label[text()='Account Name']//following::input[1]")
        );

        // Scroll into view first
        jsUtil.scrollIntoView(accountNameInput);

        // Set value and trigger change event
        jsUtil.setValueAndTriggerChange(accountNameInput, "Acme Corporation");

        // Select industry from combobox
        selectFromCombobox("Industry", "Technology");

        // Click Save (might be blocked by validation overlay)
        WebElement saveButton = driver.findElement(
            By.xpath("//button[@title='Save']")
        );
        jsUtil.clickElement(saveButton);

        // Wait for success message
        WebElement successToast = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".slds-notify--toast")
            )
        );

        String toastMessage = jsUtil.getTextContent(successToast);
        Assert.assertTrue(toastMessage.contains("was created"));
    }

    private void selectFromCombobox(String label, String value) {
        JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement combobox = driver.findElement(
            By.xpath("//label[text()='" + label + "']//ancestor::lightning-combobox")
        );
        jsUtil.clickElement(combobox);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[@title='" + value + "']")
        ));

        WebElement option = driver.findElement(
            By.xpath("//span[@title='" + value + "']")
        );
        jsUtil.clickElement(option);
    }
}
```

---

## When to Use JavascriptExecutor

### ✅ Use JavaScript When:
- Element click intercepted by overlay/spinner
- Accessing Shadow DOM elements
- Need to scroll element into view
- Setting values in Lightning components
- Removing readonly/disabled attributes
- Triggering custom events
- Debugging (highlighting elements)

### ❌ Avoid JavaScript When:
- Standard Selenium works fine
- You want to simulate real user behavior
- Testing keyboard navigation/tab order
- Validating element interactivity

---

## Performance Tip

Create JavaScriptUtil once in BaseTest:

```java
// BaseTest.java
public class BaseTest {
    protected WebDriver driver;
    protected JavaScriptUtil jsUtil;

    @BeforeMethod
    public void setUp() {
        // ... driver setup ...
        jsUtil = new JavaScriptUtil(driver);  // ← Initialize once
    }
}

// Test class
public class MyTest extends BaseTest {
    @Test
    public void test() {
        jsUtil.clickElement(element);  // ← Use directly
    }
}
```

---

## Summary

**Salesforce Lightning requires JavaScript** for:
1. Shadow DOM access
2. Overlay/spinner bypass
3. Event triggering
4. Scroll management
5. Dynamic component handling

The `JavaScriptUtil` class provides reusable methods for all common Salesforce scenarios.

---

*Added: January 13, 2026*
