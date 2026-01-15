# Actions Class Guide for Salesforce Testing

## What is Actions Class?

**Actions class** (`org.openqa.selenium.interactions.Actions`) simulates **real user interactions**:
- Mouse movements (hover, drag & drop)
- Keyboard actions (shortcuts, tab navigation)
- Complex interaction chains

**Key difference**: Actions simulates human behavior; JavascriptExecutor bypasses the browser.

---

## Three Ways to Interact with Elements

| Method | Purpose | Simulates User? | Use Case |
|--------|---------|-----------------|----------|
| **Regular Selenium** | Simple click/type | ✅ Yes | Basic interactions |
| **JavascriptExecutor** | Bypass overlays | ❌ No | Element blocked |
| **Actions Class** | Complex interactions | ✅ Yes | Hover, drag, keyboard |

---

## Quick Start

### Option 1: Use ActionsUtil (Recommended)

```java
public class MyTest extends BaseTest {
    private ActionsUtil actionsUtil;

    @BeforeMethod
    public void setUp() {
        super.setUp();
        actionsUtil = new ActionsUtil(driver);
    }

    @Test
    public void testHoverMenu() {
        WebElement menu = driver.findElement(By.id("menu"));
        actionsUtil.hoverOver(menu);
    }
}
```

### Option 2: Use Actions Directly

```java
@Test
public void testHoverMenu() {
    Actions actions = new Actions(driver);
    WebElement menu = driver.findElement(By.id("menu"));
    actions.moveToElement(menu).perform();
}
```

---

## Common Salesforce Scenarios

### 1. Hover to Reveal Menu (Most Common!)

Many Salesforce menus only appear on hover.

**Using ActionsUtil**:
```java
@Test
public void testAppLauncher() {
    WebElement appLauncher = driver.findElement(By.cssSelector(".slds-icon-waffle"));
    actionsUtil.hoverOver(appLauncher);

    // Wait for menu to appear
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//a[text()='Sales']")
    ));

    driver.findElement(By.xpath("//a[text()='Sales']")).click();
}
```

**Using Actions directly**:
```java
@Test
public void testAppLauncher() {
    Actions actions = new Actions(driver);
    WebElement appLauncher = driver.findElement(By.cssSelector(".slds-icon-waffle"));

    actions.moveToElement(appLauncher).perform();

    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//a[text()='Sales']")
    ));

    driver.findElement(By.xpath("//a[text()='Sales']")).click();
}
```

---

### 2. Hover Then Click (Nested Menus)

**Using ActionsUtil**:
```java
@Test
public void testNestedMenu() {
    WebElement mainMenu = driver.findElement(By.id("menu"));
    WebElement submenu = driver.findElement(By.id("submenu"));

    actionsUtil.hoverAndClick(mainMenu, submenu);
}
```

**Using Actions directly**:
```java
@Test
public void testNestedMenu() {
    Actions actions = new Actions(driver);
    WebElement mainMenu = driver.findElement(By.id("menu"));
    WebElement submenu = driver.findElement(By.id("submenu"));

    actions.moveToElement(mainMenu)
           .pause(Duration.ofMillis(500))
           .moveToElement(submenu)
           .click()
           .perform();
}
```

---

### 3. Actions Menu (3 Dots - Appears on Hover)

```java
public void clickRecordAction(String recordName, String action) {
    // Find the record row
    WebElement row = driver.findElement(
        By.xpath("//a[@title='" + recordName + "']//ancestor::tr")
    );

    // Hover to reveal actions menu
    actionsUtil.hoverOver(row);

    // Click the 3-dot menu
    WebElement actionMenu = row.findElement(
        By.cssSelector("button[title='Show actions for this item']")
    );
    actionMenu.click();

    // Wait for menu
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//a[@title='" + action + "']")
    ));

    // Click action
    driver.findElement(By.xpath("//a[@title='" + action + "']")).click();
}
```

---

### 4. Drag and Drop (Reorder Lists)

Salesforce allows reordering columns, dashboard components, etc.

**Using ActionsUtil**:
```java
@Test
public void testReorderColumns() {
    WebElement column1 = driver.findElement(By.id("column1"));
    WebElement column2 = driver.findElement(By.id("column2"));

    actionsUtil.dragAndDrop(column1, column2);
}
```

**Using Actions directly**:
```java
@Test
public void testReorderColumns() {
    Actions actions = new Actions(driver);
    WebElement column1 = driver.findElement(By.id("column1"));
    WebElement column2 = driver.findElement(By.id("column2"));

    actions.dragAndDrop(column1, column2).perform();
}
```

**Alternative - More control**:
```java
actions.clickAndHold(column1)
       .moveToElement(column2)
       .release()
       .perform();
```

---

### 5. Right Click (Context Menu)

**Using ActionsUtil**:
```java
@Test
public void testContextMenu() {
    WebElement element = driver.findElement(By.id("item"));
    actionsUtil.rightClick(element);

    // Wait for context menu
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector(".context-menu")
    ));

    driver.findElement(By.xpath("//span[text()='Delete']")).click();
}
```

---

### 6. Double Click (Edit Inline)

**Using ActionsUtil**:
```java
@Test
public void testInlineEdit() {
    WebElement cell = driver.findElement(
        By.xpath("//td[@data-label='Amount']")
    );

    actionsUtil.doubleClick(cell);

    // Wait for edit mode
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector("input.edit-field")
    ));

    driver.findElement(By.cssSelector("input.edit-field")).sendKeys("1000");
}
```

---

### 7. Keyboard Navigation (Tab Through Form)

**Using ActionsUtil**:
```java
@Test
public void testFormNavigation() {
    // Click first field
    driver.findElement(By.id("firstName")).click();

    // Fill using keyboard
    actionsUtil.sendKeys("John");
    actionsUtil.pressTab();         // Next field

    actionsUtil.sendKeys("Doe");
    actionsUtil.pressTab();

    actionsUtil.sendKeys("john@example.com");
    actionsUtil.pressEnter();       // Submit
}
```

**Using Actions directly**:
```java
@Test
public void testFormNavigation() {
    Actions actions = new Actions(driver);
    driver.findElement(By.id("firstName")).click();

    actions.sendKeys("John")
           .sendKeys(Keys.TAB)
           .sendKeys("Doe")
           .sendKeys(Keys.TAB)
           .sendKeys("john@example.com")
           .sendKeys(Keys.ENTER)
           .perform();
}
```

---

### 8. Keyboard Shortcuts

**Using ActionsUtil**:
```java
@Test
public void testKeyboardShortcuts() {
    WebElement textArea = driver.findElement(By.id("description"));
    textArea.sendKeys("Some text to copy");

    // Select all
    actionsUtil.selectAll();

    // Copy
    actionsUtil.copy();

    // Move to another field
    driver.findElement(By.id("notes")).click();

    // Paste
    actionsUtil.paste();
}
```

**Using Actions directly**:
```java
@Test
public void testKeyboardShortcuts() {
    Actions actions = new Actions(driver);
    WebElement textArea = driver.findElement(By.id("description"));
    textArea.sendKeys("Some text to copy");

    // Select all (Ctrl+A / Cmd+A)
    actions.keyDown(Keys.CONTROL)
           .sendKeys("a")
           .keyUp(Keys.CONTROL)
           .perform();

    // Copy (Ctrl+C / Cmd+C)
    actions.keyDown(Keys.CONTROL)
           .sendKeys("c")
           .keyUp(Keys.CONTROL)
           .perform();

    driver.findElement(By.id("notes")).click();

    // Paste (Ctrl+V / Cmd+V)
    actions.keyDown(Keys.CONTROL)
           .sendKeys("v")
           .keyUp(Keys.CONTROL)
           .perform();
}
```

---

### 9. Scroll to Element

**Using ActionsUtil** (Selenium 4+):
```java
@Test
public void testScrollToFooter() {
    WebElement footer = driver.findElement(By.id("footer"));
    actionsUtil.scrollToElement(footer);
}
```

**Using Actions directly**:
```java
@Test
public void testScrollToFooter() {
    Actions actions = new Actions(driver);
    WebElement footer = driver.findElement(By.id("footer"));
    actions.scrollToElement(footer).perform();
}
```

---

### 10. Complex Action Chains

**Using Actions directly** (custom complex interactions):
```java
@Test
public void testComplexInteraction() {
    Actions actions = new Actions(driver);

    actions.moveToElement(menu)              // 1. Hover menu
           .pause(Duration.ofMillis(500))    // 2. Wait for animation
           .moveToElement(submenu)           // 3. Hover submenu
           .pause(Duration.ofMillis(300))    // 4. Wait
           .click()                          // 5. Click
           .perform();                       // Execute all
}
```

---

## Important Notes

### Always Call `.perform()`!

```java
// ❌ WRONG - Actions not executed
actions.moveToElement(element).click();

// ✅ CORRECT - Must call .perform()
actions.moveToElement(element).click().perform();
```

### `.build()` is Optional

```java
// Modern Selenium (3.x+) - .perform() calls .build() automatically
actions.moveToElement(element).click().perform();

// Old style - .build() explicit (not needed anymore)
actions.moveToElement(element).click().build().perform();
```

### When to Use `.build()`

Only if reusing the same action:

```java
// Build once, execute multiple times
Action myAction = actions.moveToElement(element).click().build();

myAction.perform();  // First time
// ... do something ...
myAction.perform();  // Execute again
```

---

## Decision Guide: Which Method to Use?

### Use Regular Selenium When:
✅ Simple click/type
✅ Element is visible and clickable
✅ No special interaction needed

```java
driver.findElement(By.id("submit")).click();
```

---

### Use JavascriptExecutor When:
✅ Element blocked by overlay/spinner
✅ Need to access Shadow DOM
✅ Setting values directly
✅ Don't need to simulate user behavior

```java
jsUtil.clickElement(button);  // Bypass overlay
```

---

### Use Actions When:
✅ **Hover menus** (most common in Salesforce!)
✅ **Drag and drop**
✅ **Right-click / double-click**
✅ **Keyboard navigation**
✅ **Keyboard shortcuts**
✅ **Need to simulate real user behavior**

```java
actionsUtil.hoverOver(menu);
actionsUtil.dragAndDrop(source, target);
actionsUtil.pressTab();
```

---

## Real-World Example: Complete Salesforce Test

```java
package com.fsc.tests;

import com.fsc.base.BaseTest;
import com.fsc.utils.ActionsUtil;
import com.fsc.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class SalesforceNavigationTest extends BaseTest {
    private ActionsUtil actionsUtil;
    private WebDriverWait wait;

    @BeforeMethod
    public void init() {
        actionsUtil = new ActionsUtil(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void testNavigateToAccountsViaAppLauncher() {
        // Login
        driver.get(ConfigReader.getSalesforceUrl());
        // ... login code ...

        // Click App Launcher
        WebElement appLauncher = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("div.slds-icon-waffle")
            )
        );
        appLauncher.click();

        // Wait for App Launcher panel
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@placeholder='Search apps and items...']")
        ));

        // Hover over Sales app to see details
        WebElement salesApp = driver.findElement(
            By.xpath("//p[text()='Sales']//ancestor::a")
        );
        actionsUtil.hoverOver(salesApp);

        // Click to open Sales app
        salesApp.click();

        // Wait for navigation
        wait.until(ExpectedConditions.urlContains("lightning/page/home"));

        // Navigate to Accounts using keyboard
        actionsUtil.sendKeys("g");  // Salesforce keyboard shortcut
        actionsUtil.sendKeys("a");  // "a" for Accounts

        // Verify we're on Accounts page
        wait.until(ExpectedConditions.urlContains("Account/list"));
    }

    @Test
    public void testEditAccountWithDoubleClick() {
        // Navigate to Accounts list
        // ... navigation code ...

        // Find first account in list
        WebElement firstAccount = driver.findElement(
            By.xpath("(//table//tbody//tr)[1]//td[@data-label='Account Name']")
        );

        // Double-click to edit inline
        actionsUtil.doubleClick(firstAccount);

        // Wait for edit mode
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input.edit-input")
        ));

        // Edit the name
        WebElement editInput = driver.findElement(By.cssSelector("input.edit-input"));
        editInput.clear();
        editInput.sendKeys("Updated Account Name");

        // Press Enter to save
        actionsUtil.pressEnter();

        // Wait for save confirmation
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(text(),'saved')]")
        ));
    }
}
```

---

## Comparison Table

| Scenario | Regular Selenium | JavaScript | Actions |
|----------|-----------------|------------|---------|
| Simple click | ✅ Best | ⚠️ Overkill | ⚠️ Overkill |
| Blocked by overlay | ❌ Fails | ✅ Best | ❌ May fail |
| Hover menu | ❌ Can't | ❌ No events | ✅ Best |
| Drag & drop | ❌ Can't | ⚠️ Complex | ✅ Best |
| Right-click | ❌ Can't | ⚠️ Complex | ✅ Best |
| Keyboard shortcuts | ⚠️ Limited | ⚠️ Complex | ✅ Best |
| Shadow DOM | ❌ Can't | ✅ Can | ❌ Can't |
| Tab navigation | ⚠️ sendKeys() | ❌ No events | ✅ Best |
| Real user simulation | ✅ Yes | ❌ No | ✅ Yes |

---

## Summary

**Actions class is essential for**:
1. ✅ Hover menus (very common in Salesforce)
2. ✅ Drag and drop operations
3. ✅ Keyboard navigation and shortcuts
4. ✅ Right-click/double-click interactions
5. ✅ Any interaction requiring mouse movement

**Use ActionsUtil for convenience, or use Actions directly to learn the native API first.**

---

*Created: January 13, 2026*
