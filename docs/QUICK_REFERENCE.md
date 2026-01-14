# Selenium Quick Reference Guide

Quick reference for common Selenium operations, especially for Salesforce Lightning testing.

---

## Table of Contents
1. [FindElement - Locator Strategies](#findelement---locator-strategies)
2. [Waits (Implicit, Explicit, Fluent)](#waits)
3. [Salesforce Lightning Input Elements](#salesforce-lightning-input-elements)
4. [Common Patterns](#common-patterns)

---

## FindElement - Locator Strategies

### Basic Locator Methods

```java
// By ID (fastest, most reliable if available)
driver.findElement(By.id("username"));

// By Name
driver.findElement(By.name("password"));

// By Class Name
driver.findElement(By.className("slds-button"));

// By Tag Name
driver.findElement(By.tagName("button"));

// By Link Text (exact match)
driver.findElement(By.linkText("Forgot Password?"));

// By Partial Link Text
driver.findElement(By.partialLinkText("Forgot"));

// By CSS Selector (recommended for complex queries)
driver.findElement(By.cssSelector("input[type='email']"));

// By XPath (most powerful but slower)
driver.findElement(By.xpath("//input[@type='email']"));
```

### FindElement vs FindElements

```java
// findElement() - Returns single WebElement (throws exception if not found)
WebElement element = driver.findElement(By.id("username"));

// findElements() - Returns List<WebElement> (returns empty list if not found)
List<WebElement> elements = driver.findElements(By.className("item"));

// Check if element exists without exception
boolean isPresent = driver.findElements(By.id("optional")).size() > 0;
```

### CSS Selectors (Recommended for Salesforce)

```java
// By attribute
By.cssSelector("input[name='username']")
By.cssSelector("input[type='email']")

// By class
By.cssSelector(".slds-button")
By.cssSelector(".slds-input")

// Multiple classes
By.cssSelector(".slds-button.slds-button--brand")

// Starts with (for dynamic IDs)
By.cssSelector("input[id^='input-']")        // id starts with 'input-'
By.cssSelector("div[class^='slds-']")        // class starts with 'slds-'

// Ends with
By.cssSelector("input[id$='-email']")        // id ends with '-email'

// Contains
By.cssSelector("input[id*='email']")         // id contains 'email'

// Child combinator
By.cssSelector("form > input")               // Direct child
By.cssSelector("form input")                 // Descendant

// Multiple attributes
By.cssSelector("input[type='text'][name='firstName']")

// Pseudo-classes
By.cssSelector("input:enabled")
By.cssSelector("input:disabled")
By.cssSelector("li:first-child")
By.cssSelector("li:last-child")
By.cssSelector("li:nth-child(2)")
```

### XPath (For Complex Scenarios)

```java
// Absolute XPath (fragile, not recommended)
By.xpath("/html/body/div/form/input")

// Relative XPath (recommended)
By.xpath("//input[@id='username']")

// Contains text
By.xpath("//button[contains(text(),'Login')]")
By.xpath("//span[contains(text(),'Submit')]")

// Starts with (for dynamic IDs)
By.xpath("//input[starts-with(@id,'input-')]")

// Multiple conditions (AND)
By.xpath("//input[@type='text' and @name='email']")

// Multiple conditions (OR)
By.xpath("//input[@type='text' or @type='email']")

// Following sibling
By.xpath("//label[text()='Email']/following-sibling::input")

// Parent element
By.xpath("//input[@id='email']/parent::div")

// Ancestor
By.xpath("//input[@id='email']/ancestor::form")

// Contains attribute value
By.xpath("//div[contains(@class,'slds-modal')]")

// Not contains
By.xpath("//button[not(contains(@class,'disabled'))]")

// Index
By.xpath("(//input[@type='text'])[1]")      // First input
By.xpath("(//button)[last()]")              // Last button
```

### Advanced Locator Techniques

```java
// Chain locators - find element within element
WebElement form = driver.findElement(By.id("loginForm"));
WebElement username = form.findElement(By.name("username"));

// Find multiple elements and iterate
List<WebElement> buttons = driver.findElements(By.tagName("button"));
for (WebElement button : buttons) {
    if (button.getText().equals("Submit")) {
        button.click();
        break;
    }
}

// Custom attribute
By.cssSelector("[data-id='custom-field']")
By.xpath("//input[@data-test-id='email-input']")
```

---

## Waits

### 1. Implicit Wait (Global Setting)

```java
// Set once in @BeforeMethod or setUp()
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

// Applies to ALL findElement() calls
// NOT recommended for production - mixing with explicit waits causes issues
```

**Pros**:
- Simple, set once
- Applies globally

**Cons**:
- Can slow down negative tests (when checking element doesn't exist)
- Less control
- Conflicts with explicit waits

---

### 2. Explicit Wait (Recommended)

Wait for specific conditions before proceeding.

```java
// Create WebDriverWait instance
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

// Wait for element to be visible
WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

// Wait for element to be clickable
WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("loginBtn")));

// Wait for element to be present (in DOM but may not be visible)
WebElement hidden = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("hidden")));

// Wait for text to be present in element
wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "Success"));

// Wait for element to be invisible
wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loading")));

// Wait for element to be selected (checkbox/radio)
wait.until(ExpectedConditions.elementToBeSelected(By.id("agree")));

// Wait for alert to be present
wait.until(ExpectedConditions.alertIsPresent());

// Wait for frame to be available and switch to it
wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("frameName"));

// Wait for URL to contain
wait.until(ExpectedConditions.urlContains("dashboard"));

// Wait for URL to be
wait.until(ExpectedConditions.urlToBe("https://example.com/home"));

// Wait for title to contain
wait.until(ExpectedConditions.titleContains("Home"));

// Wait for number of windows to be
wait.until(ExpectedConditions.numberOfWindowsToBe(2));
```

### Common ExpectedConditions

| Condition | Input Type | Description |
|-----------|------------|-------------|
| `visibilityOfElementLocated(By)` | By | Find + wait for visible |
| `visibilityOf(WebElement)` | WebElement | Wait for already-found element |
| `presenceOfElementLocated(By)` | By | Find + wait in DOM |
| `elementToBeClickable(By)` | By | Find + wait clickable |
| `invisibilityOfElementLocated(By)` | By | Wait for hidden/removed |
| `urlContains(String)` | String | Wait for URL change |
| `titleContains(String)` | String | Wait for title |

**Rule**: `*Located` methods take `By`, others take `WebElement`

### Custom Wait Conditions

```java
// Wait with custom condition using lambda
wait.until(driver -> driver.findElement(By.id("username")).isDisplayed());

// Wait for element count
wait.until(driver -> driver.findElements(By.className("item")).size() > 5);

// Wait for attribute value
wait.until(driver -> {
    WebElement element = driver.findElement(By.id("status"));
    return element.getAttribute("class").contains("complete");
});

// Custom condition with explicit return
wait.until(new ExpectedCondition<Boolean>() {
    public Boolean apply(WebDriver driver) {
        WebElement element = driver.findElement(By.id("progress"));
        String progress = element.getText();
        return progress.equals("100%");
    }
});
```

---

### 3. Fluent Wait (Most Flexible)

Like explicit wait but with polling interval and exception handling.

```java
// Create FluentWait
Wait<WebDriver> wait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))           // Maximum wait time
    .pollingEvery(Duration.ofSeconds(2))           // Check every 2 seconds
    .ignoring(NoSuchElementException.class)        // Ignore this exception
    .ignoring(StaleElementReferenceException.class);

// Use it
WebElement element = wait.until(driver -> driver.findElement(By.id("dynamic-element")));

// With custom message
Wait<WebDriver> wait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofMillis(500))
    .withMessage("Element not found after 30 seconds");
```

### Wait Strategy Comparison

| Wait Type | Use Case | Performance |
|-----------|----------|-------------|
| **Implicit** | Quick scripts, not recommended for production | Slowest |
| **Explicit** | Production code, specific conditions | Fast |
| **Fluent** | Complex scenarios with polling needs | Flexible |

---

### Best Practices for Waits

```java
// ✅ GOOD: Use explicit wait before interaction
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
button.click();

// ❌ BAD: Use Thread.sleep() (NEVER do this!)
Thread.sleep(5000);  // Always waits full time, wastes time, unreliable, blocks thread
driver.findElement(By.id("submit")).click();

// ✅ GOOD: Wait for URL change (e.g., after login)
wait.until(driver -> driver.getCurrentUrl().contains("dashboard"));

// ✅ GOOD: Wait for loading spinner to disappear
wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading-spinner")));

// ✅ GOOD: Reusable wait method
public WebElement waitForElement(By locator, int timeoutSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

---

## Salesforce Lightning Input Elements

Salesforce Lightning uses custom components with Shadow DOM, making them tricky to interact with.

### Lightning Input Types

```html
<!-- Standard Lightning Input -->
<lightning-input class="slds-form-element" data-field="FirstName"></lightning-input>

<!-- Lightning Combobox (Dropdown) -->
<lightning-combobox class="slds-form-element" data-field="Status"></lightning-combobox>

<!-- Lightning Textarea -->
<lightning-textarea data-field="Description"></lightning-textarea>

<!-- Lightning Checkbox -->
<lightning-input type="checkbox" data-field="IsActive"></lightning-input>

<!-- Lightning Date Picker -->
<lightning-input type="date" data-field="BirthDate"></lightning-input>
```

### Finding Lightning Inputs

#### Method 1: By Label Text (Recommended)

```java
// Find input by its label
By.xpath("//label[text()='First Name']/following::input")
By.xpath("//label[contains(text(),'Email')]/parent::*/following-sibling::input")

// Lightning-specific
By.xpath("//label[text()='Account Name']//ancestor::lightning-input//input")
```

#### Method 2: By Data Attribute

```java
// If developers add data-test-id or data-field attributes
By.cssSelector("lightning-input[data-field='FirstName']")
By.cssSelector("[data-test-id='email-input']")
```

#### Method 3: By Class (Less Reliable)

```java
By.cssSelector("input.slds-input")
By.cssSelector(".slds-combobox__input")
```

### Interacting with Lightning Inputs

#### Standard Text Input

```java
// Wait and enter text
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//label[text()='First Name']//ancestor::lightning-input//input")
));
input.clear();
input.sendKeys("John");
```

#### Lightning Combobox (Dropdown)

```java
// Method 1: Click and select by text
// Click the combobox to open dropdown
driver.findElement(By.xpath("//label[text()='Status']//ancestor::lightning-combobox")).click();

// Wait for dropdown options to appear
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//span[@class='slds-media__body']")
));

// Click the desired option
driver.findElement(By.xpath("//span[text()='Active']")).click();

// Method 2: Type to search and select
WebElement combobox = driver.findElement(
    By.xpath("//label[text()='Account']//ancestor::lightning-combobox//input")
);
combobox.sendKeys("Test Account");
combobox.sendKeys(Keys.ENTER);
```

#### Lightning Checkbox

```java
// Find checkbox by label
WebElement checkbox = driver.findElement(
    By.xpath("//span[text()='Active']//ancestor::lightning-input//input[@type='checkbox']")
);

// Check if not already checked
if (!checkbox.isSelected()) {
    checkbox.click();
}

// Uncheck if checked
if (checkbox.isSelected()) {
    checkbox.click();
}
```

#### Lightning Textarea

```java
WebElement textarea = driver.findElement(
    By.xpath("//label[text()='Description']//ancestor::lightning-textarea//textarea")
);
textarea.clear();
textarea.sendKeys("This is a long description text for the record.");
```

#### Lightning Date Picker

```java
// Click to open date picker
WebElement dateInput = driver.findElement(
    By.xpath("//label[text()='Birth Date']//ancestor::lightning-input//input")
);
dateInput.click();

// Option 1: Type date directly (format: MM/DD/YYYY)
dateInput.sendKeys("12/25/2025");
dateInput.sendKeys(Keys.ENTER);

// Option 2: Click date in calendar
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.cssSelector(".slds-datepicker")
));
driver.findElement(By.xpath("//td[@data-value='2025-12-25']")).click();
```

### Handling Shadow DOM (Advanced)

Some Lightning components use Shadow DOM, requiring JavaScript execution.

```java
// Execute JavaScript to access Shadow DOM
WebElement host = driver.findElement(By.cssSelector("lightning-input"));
WebElement shadowRoot = (WebElement) ((JavascriptExecutor) driver)
    .executeScript("return arguments[0].shadowRoot", host);
WebElement input = shadowRoot.findElement(By.cssSelector("input"));
input.sendKeys("Text inside shadow DOM");
```

### Common Lightning Patterns

#### Wait for Lightning Record Page to Load

```java
// Wait for record page indicator
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.cssSelector("records-lwc-detail-panel")
));

// Or wait for specific element
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//span[contains(@class,'view-lead')]")
));
```

#### Handle Lightning Modals

```java
// Wait for modal to appear
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.cssSelector("div.slds-modal")
));

// Find input within modal
WebElement modalInput = driver.findElement(
    By.xpath("//div[@role='dialog']//label[text()='Name']//following::input")
);
modalInput.sendKeys("Test Name");

// Click button in modal
driver.findElement(
    By.xpath("//div[@role='dialog']//button[@title='Save']")
).click();

// Wait for modal to close
wait.until(ExpectedConditions.invisibilityOfElementLocated(
    By.cssSelector("div.slds-modal")
));
```

#### Handle Lightning Lookup Fields

```java
// Click lookup field
WebElement lookup = driver.findElement(
    By.xpath("//label[text()='Account Name']//ancestor::lightning-lookup//input")
);
lookup.click();

// Type search text
lookup.sendKeys("Acme");

// Wait for search results
wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.cssSelector("lightning-base-combobox-item")
));

// Click first result
driver.findElement(
    By.xpath("//lightning-base-combobox-item[1]")
).click();
```

#### Handle Lightning Toast Messages

```java
// Wait for success toast
WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.cssSelector("div.slds-notify--toast")
));

// Get toast message
String message = toast.findElement(By.cssSelector("span.toastMessage")).getText();
System.out.println("Toast message: " + message);

// Wait for toast to disappear
wait.until(ExpectedConditions.invisibilityOf(toast));
```

---

## Common Patterns

### 1. Safe Element Interaction Pattern

```java
public void safeClick(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

    // Scroll into view
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

    // Small wait for scroll animation
    try {
        Thread.sleep(300);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    element.click();
}

public void safeType(By locator, String text) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    element.clear();
    element.sendKeys(text);
}
```

### 2. Verify Element Exists Without Exception

```java
public boolean isElementPresent(By locator) {
    return driver.findElements(locator).size() > 0;
}

public boolean isElementVisible(By locator) {
    try {
        return driver.findElement(locator).isDisplayed();
    } catch (NoSuchElementException e) {
        return false;
    }
}
```

### 3. Dynamic Wait for Text Change

```java
public void waitForTextChange(By locator, String oldText) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(driver -> {
        String currentText = driver.findElement(locator).getText();
        return !currentText.equals(oldText);
    });
}
```

### 4. Wait for Ajax/Loading to Complete

```java
// Wait for jQuery to complete (if page uses jQuery)
public void waitForAjax() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        return (Boolean) ((JavascriptExecutor) driver)
            .executeScript("return jQuery.active == 0");
    });
}

// Wait for spinner to disappear
public void waitForLoadingSpinner() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(
        By.cssSelector(".slds-spinner")
    ));
}
```

### 5. Stale Element Retry Pattern

```java
public void clickWithRetry(By locator, int attempts) {
    for (int i = 0; i < attempts; i++) {
        try {
            driver.findElement(locator).click();
            break;
        } catch (StaleElementReferenceException e) {
            if (i == attempts - 1) {
                throw e;
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

### 6. Switch to iFrame and Back

```java
// Switch to iframe
driver.switchTo().frame("iframeName");
// or
driver.switchTo().frame(driver.findElement(By.id("iframe-id")));

// Do something in iframe
driver.findElement(By.id("element-in-iframe")).click();

// Switch back to main content
driver.switchTo().defaultContent();
```

### 7. Browser Options vs Window Management

**ChromeOptions** (Before browser starts):
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--start-maximized");  // Opens maximized
options.addArguments("--headless");         // No GUI (for CI/CD)
driver = new ChromeDriver(options);
```

**Window Handler** (After browser starts):
```java
driver.manage().window().maximize();           // Maximize
driver.manage().window().setSize(new Dimension(1920, 1080));
driver.manage().window().setPosition(new Point(0, 0));
```

**When to use ChromeOptions**: Headless, maximize, fixed config
**When to use Window Handler**: Dynamic sizing, responsive testing

**Headless Mode**: Essential for CI/CD servers (no displays), Docker, remote servers

---

## Quick Tips

### Performance Tips
- **Prefer CSS selectors** over XPath when possible (faster)
- **Use explicit waits** instead of implicit waits
- **Avoid Thread.sleep()** - always use proper waits
- **Reuse WebDriverWait** objects when possible

### Reliability Tips
- **Always wait before interaction** - elements may not be immediately ready
- **Check element state** before interaction (enabled, visible, clickable)
- **Handle stale elements** with retry logic
- **Use unique locators** - avoid brittle locators that depend on structure

### Salesforce-Specific Tips
- **Use label text** to find inputs (most reliable in Lightning)
- **Wait for spinners** to disappear before assertions
- **Handle toast messages** - they can block clicks
- **Test in both Classic and Lightning** if applicable
- **Use data attributes** when available (ask developers to add them)

---

## Debugging Tips

```java
// Print element details
WebElement element = driver.findElement(By.id("test"));
System.out.println("Tag: " + element.getTagName());
System.out.println("Text: " + element.getText());
System.out.println("Displayed: " + element.isDisplayed());
System.out.println("Enabled: " + element.isEnabled());
System.out.println("Class: " + element.getAttribute("class"));

// Take screenshot on failure
public void takeScreenshot(String fileName) {
    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    try {
        FileUtils.copyFile(screenshot, new File("screenshots/" + fileName + ".png"));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Highlight element (for debugging)
public void highlightElement(WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].style.border='3px solid red'", element);
}
```

---

## Understanding Standard vs Custom Code

**Reusable Across All Projects** (Standard Selenium):
- `WebDriverWait`, `ExpectedConditions`, `By` methods - Universal Selenium classes/methods
- Import from: `org.openqa.selenium.*` packages

**Project-Specific** (Your Custom Code):
- Locators like `By.id("error")` - The "error" ID is specific to your website
- Page classes like `SalesforceLoginPage` - Built for your application
- Pattern is universal, locator values change per website

---

*Updated: January 13, 2026*
