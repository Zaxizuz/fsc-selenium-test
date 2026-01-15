# Selenium Exception Handling Guide

## What Are Exceptions?

**Exceptions** are errors that occur during test execution. Selenium throws specific exceptions to tell you what went wrong.

**All Selenium exceptions extend**: `org.openqa.selenium.WebDriverException`

---

## Top 10 Most Common Selenium Exceptions

### 1. **NoSuchElementException** (Most Common!)

**When it happens**: Element doesn't exist in the DOM

```java
// ❌ Element not found - throws exception
driver.findElement(By.id("nonExistent")).click();
```

**Error message**:
```
NoSuchElementException: Unable to locate element: {"method":"css selector","selector":"#nonExistent"}
```

**Common causes**:
- Element not loaded yet (page still loading)
- Wrong locator (typo, incorrect ID/class)
- Element in iframe (need to switch first)
- Element in Shadow DOM (need JavaScript)
- Dynamic ID changed

**Solutions**:

```java
// ✅ Solution 1: Use explicit wait
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.id("myElement"))
);
element.click();

// ✅ Solution 2: Check if element exists before interacting
if (driver.findElements(By.id("myElement")).size() > 0) {
    driver.findElement(By.id("myElement")).click();
} else {
    System.out.println("Element not found!");
}

// ✅ Solution 3: Try-catch for optional elements
try {
    driver.findElement(By.id("optionalElement")).click();
} catch (NoSuchElementException e) {
    System.out.println("Optional element not present, continuing...");
}
```

**Salesforce example**:
```java
// Wait for element after Lightning page transition
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
WebElement newButton = wait.until(
    ExpectedConditions.presenceOfElementLocated(
        By.xpath("//button[@title='New']")
    )
);
newButton.click();
```

---

### 2. **ElementNotInteractableException**

**When it happens**: Element exists but can't be interacted with (hidden, disabled, or covered)

```java
// ❌ Element exists but is hidden
driver.findElement(By.id("hiddenElement")).click();
```

**Error message**:
```
ElementNotInteractableException: element not interactable
```

**Common causes**:
- Element has `display: none` or `visibility: hidden`
- Element is disabled (`disabled` attribute)
- Element is covered by another element (overlay, modal)
- Element has zero dimensions
- Element is off-screen

**Solutions**:

```java
// ✅ Solution 1: Wait for element to be clickable
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(
    ExpectedConditions.elementToBeClickable(By.id("myButton"))
);
element.click();

// ✅ Solution 2: Scroll element into view first
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement element = driver.findElement(By.id("myButton"));
jsUtil.scrollIntoView(element);
element.click();

// ✅ Solution 3: Use JavaScript click (bypasses visibility check)
WebElement element = driver.findElement(By.id("myButton"));
jsUtil.clickElement(element);

// ✅ Solution 4: Wait for overlay to disappear
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector(".loading-overlay")
));
driver.findElement(By.id("myButton")).click();
```

**Salesforce example**:
```java
// Wait for spinner to disappear, then click
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector(".slds-spinner")
));

WebElement saveButton = wait.until(
    ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Save']"))
);
saveButton.click();
```

---

### 3. **StaleElementReferenceException**

**When it happens**: Element reference is outdated (DOM refreshed, element removed/recreated)

```java
// ❌ Element reference becomes stale
WebElement element = driver.findElement(By.id("myElement"));
driver.navigate().refresh();  // Page refreshed!
element.click();  // Stale! Element no longer valid
```

**Error message**:
```
StaleElementReferenceException: stale element reference: element is not attached to the page document
```

**Common causes**:
- Page refreshed or navigated
- AJAX request re-rendered the DOM
- Element removed and re-added to DOM
- Lightning page transition in Salesforce

**Solutions**:

```java
// ✅ Solution 1: Re-find the element
WebElement element = driver.findElement(By.id("myElement"));
// ... page changes ...
element = driver.findElement(By.id("myElement"));  // Find again
element.click();

// ✅ Solution 2: Retry with try-catch
public void clickWithRetry(By locator, int attempts) {
    for (int i = 0; i < attempts; i++) {
        try {
            driver.findElement(locator).click();
            break;  // Success, exit loop
        } catch (StaleElementReferenceException e) {
            if (i == attempts - 1) {
                throw e;  // Last attempt failed, throw exception
            }
            // Retry
        }
    }
}

// Usage
clickWithRetry(By.id("myButton"), 3);

// ✅ Solution 3: Wait and re-find
public WebElement waitAndRefind(By locator, Duration timeout) {
    WebDriverWait wait = new WebDriverWait(driver, timeout);
    return wait.until(driver -> {
        try {
            return driver.findElement(locator);
        } catch (StaleElementReferenceException e) {
            return null;
        }
    });
}
```

**Salesforce example**:
```java
// Salesforce Lightning often re-renders elements
public void clickSalesforceElement(By locator) {
    int attempts = 0;
    while (attempts < 3) {
        try {
            WebElement element = driver.findElement(locator);
            element.click();
            break;
        } catch (StaleElementReferenceException e) {
            attempts++;
            if (attempts == 3) throw e;
        }
    }
}
```

---

### 4. **ElementClickInterceptedException**

**When it happens**: Element exists and is visible, but another element receives the click

```java
// ❌ Element blocked by overlay
driver.findElement(By.id("button")).click();
```

**Error message**:
```
ElementClickInterceptedException: Element <button> is not clickable at point (x, y).
Other element would receive the click: <div class="overlay">
```

**Common causes**:
- Loading spinner covering element
- Modal/dialog in front
- Cookie consent banner
- Floating header blocking element
- Toast notification in the way

**Solutions**:

```java
// ✅ Solution 1: Wait for blocking element to disappear
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector(".loading-spinner")
));
driver.findElement(By.id("button")).click();

// ✅ Solution 2: Use JavaScript click (bypasses interception)
JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement button = driver.findElement(By.id("button"));
jsUtil.clickElement(button);

// ✅ Solution 3: Scroll element into view
WebElement button = driver.findElement(By.id("button"));
jsUtil.scrollIntoView(button);
Thread.sleep(300);  // Wait for scroll animation
button.click();

// ✅ Solution 4: Use Actions class
Actions actions = new Actions(driver);
WebElement button = driver.findElement(By.id("button"));
actions.moveToElement(button).click().perform();
```

**Salesforce example**:
```java
// Wait for toast message to disappear before clicking
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector("div.slds-notify--toast")
));

// Now click the button
driver.findElement(By.xpath("//button[@title='Edit']")).click();
```

---

### 5. **TimeoutException**

**When it happens**: Wait condition not met within timeout period

```java
// ❌ Element never appears
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.id("neverAppears")
));  // Throws TimeoutException after 5 seconds
```

**Error message**:
```
TimeoutException: Expected condition failed: waiting for visibility of element located by By.id: neverAppears
```

**Common causes**:
- Element takes longer than timeout to appear
- Element never appears (wrong locator)
- Network slow
- Wrong expected condition

**Solutions**:

```java
// ✅ Solution 1: Increase timeout
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
WebElement element = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("slowElement"))
);

// ✅ Solution 2: Try-catch to handle gracefully
try {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("optional")));
} catch (TimeoutException e) {
    System.out.println("Element not found within timeout, continuing...");
}

// ✅ Solution 3: Use different expected condition
// Instead of visibilityOfElementLocated (requires visible)
wait.until(ExpectedConditions.presenceOfElementLocated(By.id("element")));  // Only requires in DOM

// ✅ Solution 4: Custom wait message
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.withMessage("Failed to find Save button after 10 seconds");
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("save")));
```

---

### 6. **NoSuchFrameException**

**When it happens**: Trying to switch to frame that doesn't exist

```java
// ❌ Frame doesn't exist
driver.switchTo().frame("nonExistentFrame");
```

**Error message**:
```
NoSuchFrameException: No frame element found by name or id: nonExistentFrame
```

**Solutions**:

```java
// ✅ Solution 1: Wait for frame to be available
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("frameName"));

// ✅ Solution 2: Find frame element first
WebElement frameElement = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.id("myFrame"))
);
driver.switchTo().frame(frameElement);

// ✅ Solution 3: Switch by index (if you know the position)
driver.switchTo().frame(0);  // First frame
```

**Salesforce example**:
```java
// Salesforce Visualforce pages use iframes
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
    By.cssSelector("iframe[title='accessibility title']")
));

// Do something in frame
driver.findElement(By.id("elementInFrame")).click();

// Switch back to main content
driver.switchTo().defaultContent();
```

---

### 7. **NoSuchWindowException**

**When it happens**: Trying to switch to window that doesn't exist

```java
// ❌ Window handle invalid
driver.switchTo().window("invalidWindowHandle");
```

**Solutions**:

```java
// ✅ Solution 1: Get current window handles
String mainWindow = driver.getWindowHandle();
Set<String> allWindows = driver.getWindowHandles();

for (String window : allWindows) {
    if (!window.equals(mainWindow)) {
        driver.switchTo().window(window);
        break;
    }
}

// ✅ Solution 2: Wait for new window
String originalWindow = driver.getWindowHandle();
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.numberOfWindowsToBe(2));

Set<String> allWindows = driver.getWindowHandles();
for (String window : allWindows) {
    if (!window.equals(originalWindow)) {
        driver.switchTo().window(window);
        break;
    }
}
```

---

### 8. **NoAlertPresentException**

**When it happens**: Trying to interact with alert that doesn't exist

```java
// ❌ No alert present
driver.switchTo().alert().accept();
```

**Solutions**:

```java
// ✅ Solution 1: Wait for alert
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
Alert alert = wait.until(ExpectedConditions.alertIsPresent());
alert.accept();

// ✅ Solution 2: Check if alert exists
try {
    driver.switchTo().alert().accept();
} catch (NoAlertPresentException e) {
    System.out.println("No alert to dismiss");
}
```

---

### 9. **InvalidSelectorException**

**When it happens**: Locator syntax is invalid

```java
// ❌ Invalid XPath syntax
driver.findElement(By.xpath("//div[@id='test'"));  // Missing closing bracket
```

**Error message**:
```
InvalidSelectorException: invalid selector: Unable to locate an element with the xpath expression
```

**Solutions**:

```java
// ✅ Fix the locator syntax
driver.findElement(By.xpath("//div[@id='test']"));  // Correct

// ✅ Validate XPath in browser console first
// Open DevTools Console and type:
// $x("//div[@id='test']")
```

---

### 10. **SessionNotFoundException / WebDriverException**

**When it happens**: Browser closed or driver session ended

```java
// ❌ Browser was closed
driver.quit();
driver.findElement(By.id("element")).click();  // Session is gone!
```

**Solutions**:

```java
// ✅ Don't interact with driver after quit()

// ✅ Check if driver is still alive
if (driver != null) {
    try {
        driver.getTitle();  // Check if session active
    } catch (WebDriverException e) {
        // Session dead, need new driver
        driver = new ChromeDriver();
    }
}
```

---

## Exception Handling Best Practices

### 1. **Use Specific Exceptions**

```java
// ❌ BAD - Catches everything
try {
    driver.findElement(By.id("element")).click();
} catch (Exception e) {
    // Too broad!
}

// ✅ GOOD - Catch specific exceptions
try {
    driver.findElement(By.id("element")).click();
} catch (NoSuchElementException e) {
    System.out.println("Element not found: " + e.getMessage());
} catch (ElementNotInteractableException e) {
    System.out.println("Element not interactable: " + e.getMessage());
}
```

---

### 2. **Prevent Rather Than Catch**

```java
// ❌ BAD - Use exception as flow control
try {
    driver.findElement(By.id("element")).click();
} catch (NoSuchElementException e) {
    // Element doesn't exist, do nothing
}

// ✅ GOOD - Check existence first
if (driver.findElements(By.id("element")).size() > 0) {
    driver.findElement(By.id("element")).click();
}

// ✅ BETTER - Use explicit wait
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.id("element"))
);
element.click();
```

---

### 3. **Log Exceptions Properly**

```java
// ✅ GOOD - Detailed logging
try {
    driver.findElement(By.id("submit")).click();
} catch (ElementClickInterceptedException e) {
    System.err.println("Click intercepted on Submit button");
    System.err.println("Error: " + e.getMessage());

    // Take screenshot for debugging
    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    // Save screenshot...

    throw e;  // Re-throw for test to fail
}
```

---

### 4. **Retry Mechanism for Flaky Tests**

```java
public void clickWithRetry(By locator, int maxAttempts) {
    for (int attempt = 0; attempt < maxAttempts; attempt++) {
        try {
            WebElement element = driver.findElement(locator);
            element.click();
            return;  // Success!
        } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
            if (attempt == maxAttempts - 1) {
                throw e;  // Last attempt, throw exception
            }
            // Wait a bit before retry
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

---

## Exception Summary Table

| Exception | Cause | Prevention | Quick Fix |
|-----------|-------|------------|-----------|
| **NoSuchElementException** | Element not in DOM | Use explicit wait | `presenceOfElementLocated()` |
| **ElementNotInteractableException** | Element hidden/disabled | Wait for clickable | `elementToBeClickable()` |
| **StaleElementReferenceException** | DOM refreshed | Re-find element | Retry with fresh lookup |
| **ElementClickInterceptedException** | Element blocked | Wait for overlay to disappear | JavaScript click |
| **TimeoutException** | Wait timeout exceeded | Increase timeout | Verify locator is correct |
| **NoSuchFrameException** | Frame doesn't exist | Wait for frame | `frameToBeAvailableAndSwitchToIt()` |
| **InvalidSelectorException** | Bad locator syntax | Validate in console | Fix syntax error |

---

## Common Salesforce-Specific Exception Scenarios

### Scenario 1: Lightning Spinner Blocking Click
```java
// Wait for spinner, then click
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector(".slds-spinner")
));

JavaScriptUtil jsUtil = new JavaScriptUtil(driver);
WebElement button = driver.findElement(By.xpath("//button[@title='Save']"));
jsUtil.clickElement(button);  // JS click to bypass any remaining overlay
```

### Scenario 2: Stale Element After Navigation
```java
// Click tab (causes Lightning page transition)
driver.findElement(By.xpath("//a[@title='Opportunities']")).click();

// Wait for navigation
wait.until(ExpectedConditions.urlContains("Opportunity"));

// Re-find element after page change (avoid stale reference)
WebElement newButton = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@title='New']"))
);
newButton.click();
```

---

*Created: January 13, 2026*
