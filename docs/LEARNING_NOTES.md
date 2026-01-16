# Learning Notes - Selenium Project Setup

**Date**: January 12, 2026
**Project**: fsc-selenium-test (Salesforce Selenium Testing)

---

## Today's Progress

### 1. Understanding Java Version Requirements for Selenium

**Key Learnings**:
- **Minimum Java Version**: Java 11 (required since September 30, 2023)
- **Java 17**: Fully supported and **recommended** for better performance and security
- **My Setup**: Java 17.0.10 installed ✓

**Why Java 17 is Better**:
- Improved performance (better garbage collection & JIT compilation)
- Enhanced security features
- Modern language features
- Future-proofing (Selenium moving toward Java 17 minimum)

**Sources**:
- [Selenium Java 11 minimum requirement](https://github.com/SeleniumHQ/selenium/issues/11526)
- [Java 17 feature discussion](https://github.com/SeleniumHQ/selenium/issues/14022)

---

### 2. Understanding Maven and Why It's Essential

**What is Maven?**
- Build automation and dependency management tool
- Industry standard for Java projects

**Why Selenium Projects Need Maven**:

1. **Dependency Management** (Most Important!)
   - Without Maven: Manually download 20+ JAR files + all their dependencies
   - With Maven: Add a few lines in `pom.xml`, Maven downloads everything automatically

2. **Version Control**
   - Easy to upgrade/downgrade library versions
   - Just change one version number in `pom.xml`

3. **Team Collaboration**
   - Everyone gets the same library versions automatically
   - No "it works on my machine" problems

4. **CI/CD Integration**
   - Works seamlessly with Jenkins, GitLab CI, GitHub Actions

5. **Standard Project Structure**
   ```
   src/main/java     → Production code
   src/test/java     → Test code
   pom.xml           → Dependency configuration
   ```

**Key Maven Commands Learned**:
```bash
mvn clean install    # Clean, compile, and install dependencies
mvn test            # Run all tests
mvn test -Dtest=ClassName   # Run specific test class
```

---

### 3. TestNG vs JUnit - Choosing the Right Framework

**Decision**: Chose **TestNG** for Salesforce testing

**Comparison**:

| Feature | TestNG | JUnit 5 |
|---------|--------|---------|
| Learning Curve | Medium | Easy |
| Data-driven Tests | Excellent | Good |
| Parallel Execution | Built-in | Requires config |
| Test Dependencies | Yes | Limited |
| XML Configuration | Yes | Limited |
| Reporting | Excellent (HTML out of box) | Basic |
| Salesforce Testing | **Recommended** | Usable |

**Why TestNG for Salesforce**:
1. **Better parameterization** - Test with multiple user credentials
2. **Test dependencies** - Login → Create Account → Create Contact
3. **Parallel execution** - Run multiple tests faster
4. **Built-in reporting** - Important for enterprise testing
5. **XML configuration** - Easy to manage test suites

**TestNG Key Annotations**:
```java
@BeforeMethod   // Runs before each test method
@AfterMethod    // Runs after each test method
@Test           // Marks a test method
@Parameters     // Pass parameters from testng.xml
```

---

### 4. Maven Project Creation and Structure

**Project Created**: `fsc-selenium-test`

**Directory Structure**:
```
fsc-selenium-test/
├── pom.xml                          # Maven configuration
├── .gitignore                       # Git ignore file
├── README.md                        # Project documentation
├── src/
│   ├── main/java/
│   │   └── com/fsc/pages/
│   │       └── SalesforceLoginPage.java    # Page Object Model
│   └── test/
│       ├── java/
│       │   ├── com/fsc/base/
│       │   │   └── BaseTest.java           # Base test setup
│       │   └── com/fsc/tests/
│       │       └── SalesforceLoginTest.java # Test cases
│       └── resources/
│           ├── testng.xml                   # TestNG config
│           └── config.properties            # Configuration
```

**Key Dependencies in pom.xml**:
```xml
<dependencies>
    <!-- Selenium 4.17.0 -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.17.0</version>
    </dependency>

    <!-- TestNG 7.9.0 -->
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.9.0</version>
        <scope>test</scope>
    </dependency>

    <!-- WebDriverManager - Auto-manages browser drivers -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.6.3</version>
    </dependency>
</dependencies>
```

---

### 5. Page Object Model (POM) Pattern

**What is POM?**
- Design pattern for organizing Selenium code
- Separates page elements from test logic
- Makes code more maintainable and reusable

**Example Structure**:
```java
// Page Class (SalesforceLoginPage.java)
public class SalesforceLoginPage {
    private WebDriver driver;
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");

    public void login(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
    }
}

// Test Class (SalesforceLoginTest.java)
@Test
public void testLogin() {
    SalesforceLoginPage loginPage = new SalesforceLoginPage(driver);
    loginPage.login("user@example.com", "password");
}
```

**Benefits**:
- Code reusability
- Easy maintenance (if UI changes, update only page class)
- Better readability
- Reduced code duplication

---

### 6. Salesforce-Specific Testing Challenges

**Key Challenges**:

1. **Dynamic Elements**
   - Salesforce Lightning UI has constantly changing element IDs
   - **Solution**: Use robust locators (CSS, XPath with contains/starts-with)

2. **Shadow DOM**
   - Extra complexity in DOM structure
   - **Solution**: May require JavaScript execution to access elements

3. **iFrames**
   - Multiple layers of frames to navigate
   - **Solution**:
   ```java
   driver.switchTo().frame("frameName");
   ```

4. **Frequent Updates**
   - Salesforce updates regularly, breaking tests
   - **Solution**: Use Page Object Model for easy maintenance

**Best Practices Implemented**:
- Explicit waits for dynamic elements
- Chrome options optimized for Salesforce:
  ```java
  options.addArguments("--start-maximized");
  options.addArguments("--disable-notifications");
  options.addArguments("--disable-popup-blocking");
  ```

---

### 7. ChromeOptions vs Window Handler

**ChromeOptions** (Configure BEFORE browser starts):
- Set during driver creation: `new ChromeDriver(options)`
- For: maximize, headless, preferences
- **Better for**: Initial setup, headless mode, browser preferences

**Window Handler** (Control AFTER browser starts):
- Control existing window: `driver.manage().window()`
- For: resize, reposition, dynamic changes
- **Better for**: Responsive testing, dynamic sizing

**Headless Mode Purpose**:
- **Main reason**: CI/CD servers have no displays (Jenkins, GitHub Actions)
- Secondary: 10-15% faster, less memory
- Use cases: Docker, remote servers, parallel execution

---

### 8. ExpectedConditions Methods

**visibilityOfElementLocated(By)** vs **visibilityOf(WebElement)**:

| Method | Input | Use Case |
|--------|-------|----------|
| `visibilityOfElementLocated(By)` | By locator | Most common - finds + waits |
| `visibilityOf(WebElement)` | WebElement | Already have element reference |

**Rule**: If method name has `*Located`, it takes `By`. Otherwise, takes `WebElement`.

### 9. Troubleshooting and Debugging

#### Issue 1: "mvn command not found"
- **Solution**: Use full path `/opt/homebrew/bin/mvn` or add to PATH

#### Issue 2: TestNG annotations not found
- **Problem**: `BaseTest.java` was in `src/main/java` but TestNG has `<scope>test</scope>`
- **Root Cause**: TestNG is only available for test code, not main code
- **Solution**: Moved `BaseTest.java` to `src/test/java/com/fsc/base/`
- **Lesson**: Test-related classes should go in `src/test/java/`

#### Issue 3: Test Failure - Parameters not defined
- **Problem**: `testValidLogin` requires username/password parameters
- **Error**:
  ```
  Parameter 'username' is required by @Test on method testValidLogin
  but has not been marked @Optional or defined in testng.xml
  ```
- **Solution Options**:
  1. Add parameters to `testng.xml`:
     ```xml
     <parameter name="username" value="user@example.com"/>
     <parameter name="password" value="password"/>
     ```
  2. Or mark parameters as `@Optional` in test method
  3. Or remove `@Parameters` annotation for non-parameterized tests

#### Issue 4: Chrome CDP Warning
- **Warning**: Unable to find CDP implementation matching Chrome 143
- **Impact**: Minor warning, doesn't affect basic functionality
- **Note**: Selenium 4.17.0 doesn't have CDP for Chrome 143 yet

---

### 10. Package Imports: Standard vs Custom

**Standard Selenium** (Reusable everywhere):
- `WebDriverWait`, `ExpectedConditions`, `By` - from `org.openqa.selenium.*`
- Universal across all Selenium projects

**Custom/Project-Specific**:
- `SalesforceLoginPage` - Your page class
- Locator values like `By.id("error")` - Website-specific

**Key Insight**: Selenium methods are universal; only locator values change per website.

**Reusing Code**:
- Copy files OR create shared JAR library
- For production: Package as Maven artifact

---

### 11. Config Files vs TestNG @Parameters

**Config File Approach** (Chosen for this project):
- Simple: All settings in one `.properties` file
- Easy to gitignore credentials
- Best for: Single environment, learning, simple setup

**@Parameters Approach**:
- Data-driven: Same test, different data
- TestNG XML configuration
- Best for: Multiple users, test scenarios, parallel execution

**Decision**: Used config.properties for simplicity; can add @Parameters later for data-driven testing

---

## Key Concepts Learned

### 1. Maven Dependency Scopes
```xml
<scope>test</scope>     <!-- Only available in test code -->
<scope>compile</scope>  <!-- Available everywhere (default) -->
```

### 2. TestNG Test Configuration
- `testng.xml` - Define test suites, parameters, and execution order
- Can run different test suites for different environments

### 3. Explicit vs Implicit Waits
```java
// Implicit wait (set once, applies to all elements)
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

// Explicit wait (specific conditions)
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
```

### 4. Chrome Options for Testing
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--start-maximized");      // Full screen
options.addArguments("--disable-notifications"); // No popups
options.addArguments("--disable-popup-blocking");
```

---

## Commands Reference

### Maven Commands
```bash
mvn clean                  # Clean target directory
mvn compile                # Compile source code
mvn test                   # Run tests
mvn clean install          # Clean + compile + test + package
mvn test -Dtest=ClassName  # Run specific test class
```

### Git Commands
```bash
git init                   # Initialize repository
git add .                  # Stage all files
git commit -m "message"    # Commit changes
git push origin main       # Push to remote
git status                 # Check status
```

### GitHub CLI Commands
```bash
gh auth login              # Authenticate with GitHub
gh auth status             # Check auth status
gh repo create             # Create new repository
gh repo view               # View repository details
```

---

## Next Steps

### To Do:
1. ✓ Install Maven
2. ✓ Create Maven project structure
3. ✓ Set up TestNG configuration
4. ✓ Implement Page Object Model
5. ✓ Create GitHub repository
6. ⏳ Update `testng.xml` with actual test parameters
7. ⏳ Add Salesforce credentials to `config.properties`
8. ⏳ Run tests against actual Salesforce instance
9. ⏳ Add more page objects (Home page, Account page, etc.)
10. ⏳ Implement data-driven tests with TestNG DataProvider
11. ⏳ Set up test reporting
12. ⏳ Handle Shadow DOM elements in Salesforce
13. ⏳ Implement framework for handling iFrames

### Learning Goals:
- Master TestNG annotations and configuration
- Learn advanced Selenium techniques (Shadow DOM, iFrames)
- Understand Salesforce Lightning UI structure
- Implement robust wait strategies
- Create reusable test utilities
- Set up CI/CD pipeline (future)

---

## Resources and References

### Official Documentation
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/doc/)
- [Maven Documentation](https://maven.apache.org/guides/)

### Key Articles Read Today
1. [Java 8 support in Selenium](https://www.selenium.dev/blog/2023/java-8-support/)
2. [Maven Dependency Management with Selenium | BrowserStack](https://www.browserstack.com/guide/maven-dependency-with-selenium)
3. [Automate Salesforce Tests using Selenium | BrowserStack](https://www.browserstack.com/guide/salesforce-and-selenium)
4. [Salesforce Testing Guide 2026](https://www.headspin.io/blog/a-step-by-step-guide-to-perform-salesforce-crm-testing)

### Tools Installed Today
- ✓ Maven 3.9.12
- ✓ GitHub CLI (gh) 2.83.2
- ✓ Java 17.0.10 (already installed)

---

## Reflections

### What Went Well
- Successfully set up complete Maven project structure
- Understood the importance of Maven for dependency management
- Chose appropriate testing framework (TestNG) for Salesforce testing
- Implemented clean Page Object Model architecture
- Successfully pushed project to GitHub

### Challenges Faced
- Maven PATH configuration issue (resolved by using full path)
- Understanding TestNG scope and where test classes should go
- GitHub CLI interactive authentication (resolved)

### Key Takeaways
1. **Maven is essential** - Makes dependency management painless
2. **TestNG > JUnit for complex testing** - Better for enterprise applications like Salesforce
3. **Page Object Model is crucial** - Separation of concerns improves maintainability
4. **Salesforce has unique challenges** - Dynamic elements, Shadow DOM, iFrames require special handling
5. **Proper project structure matters** - Test code in `src/test/java`, dependencies properly scoped

---

## Session 2: Deep Dive - Config, Waits & Browser Control (January 13, 2026)

### ConfigReader & Configuration Strategy
- Created `ConfigReader.java` for centralized config management
- **Decision**: Config files over @Parameters for simplicity
- Can evolve to @Parameters for data-driven testing later

### Critical Best Practice: Waits
- **Fixed**: Replaced `Thread.sleep()` with `WebDriverWait`
- **Why Thread.sleep fails**: Always waits full time, unreliable, blocks thread
- **Proper approach**: `wait.until()` with dynamic conditions (waits only as long as needed)

### Browser Control Strategies
- **ChromeOptions**: Configure before browser starts (headless, maximize, preferences)
- **Window Handler**: Control after browser starts (resize, reposition)
- **Headless mode**: Essential for CI/CD (servers have no displays), 10-15% faster

### Understanding Code Reusability
- **Standard Selenium**: `WebDriverWait`, `ExpectedConditions` - universal across projects
- **Project-specific**: Page classes, locator values - change per website
- **ExpectedConditions methods**: `*Located(By)` vs without (WebElement)

## Session 3: Advanced Selenium & Test Reporting (January 15, 2026)

### JavascriptExecutor for Salesforce
**Why Needed**: Salesforce Lightning has overlays, Shadow DOM, and dynamic components

**Created**: `JavaScriptUtil.java` with 15+ utility methods:
- `clickElement()` - Bypass overlays/spinners blocking clicks
- `scrollIntoView()` - Handle lazy loading
- `getShadowRoot()` - Access Shadow DOM elements
- `setValueAndTriggerChange()` - Trigger Lightning component events
- `isSpinnerPresent()` - Wait for loading completion

**Key Insight**: JavascriptExecutor bypasses browser behavior (doesn't simulate users). Use for:
- ✅ Element blocked by overlay
- ✅ Shadow DOM access
- ✅ Scroll management
- ❌ Avoid when testing user interactions

### Actions Class for Complex Interactions
**Purpose**: Simulate real user behavior (mouse, keyboard)

**Created**: `ActionsUtil.java` with 20+ utility methods:
- `hoverOver()` - Reveal menus (most common in Salesforce!)
- `dragAndDrop()` - Reorder columns
- `doubleClick()` - Inline editing
- `rightClick()` - Context menus
- `pressTab()`, `selectAll()`, `copy()`, `paste()` - Keyboard navigation (OS-aware)

**Important**: Always call `.perform()` to execute actions! `.build()` is optional (auto-called by `.perform()`)

**When to Use Actions vs JavaScript**:
- **Actions**: Hover menus, drag-drop, keyboard shortcuts (simulates users)
- **JavaScript**: Bypass overlays, Shadow DOM (direct manipulation)
- **Regular Selenium**: Simple click/type (standard approach)

### Exception Handling
**Created**: `EXCEPTION_HANDLING_GUIDE.md` covering:
- Top 10 Selenium exceptions
- Root causes and prevention strategies
- **Key Principle**: Prevent exceptions (proper waits, robust locators) > Catch them

**Most Common**:
1. `NoSuchElementException` - Element not found (fix: wait for element)
2. `ElementNotInteractableException` - Element not clickable (fix: wait for clickable, or use JS)
3. `StaleElementReferenceException` - Element changed (fix: re-find element)
4. `TimeoutException` - Wait timeout (fix: increase wait time, check condition)

### Test Reporting with Extent Reports
**Added**: Extent Reports 5.1.1 for comprehensive HTML test reports

**Created**:
- `ExtentReportManager.java` - Report lifecycle management
- `TestListener.java` - Automatic test logging via TestNG hooks
- Updated `testng.xml` with listener configuration

**Features**:
- Timestamped HTML reports in `test-output/extent-reports/`
- Automatic screenshot capture on test failure
- Color-coded results (green=pass, red=fail, yellow=skip)
- System information (browser, OS, Java version, Salesforce URL)
- No manual logging needed - fully automatic!

### Salesforce Automation Detection & Bypass
**Problem**: Salesforce detects Selenium and requires email verification codes

**Root Cause**: Salesforce checks:
- `navigator.webdriver === true` (set by Selenium)
- Browser fingerprinting (missing plugins, automation signals)
- Behavioral patterns (typing speed, mouse movements)

**Solutions Implemented**:
1. **Anti-detection ChromeOptions**:
   ```java
   options.addArguments("--disable-blink-features=AutomationControlled");
   options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
   ```
2. **Hide webdriver flag**:
   ```java
   ((JavascriptExecutor) driver).executeScript(
       "Object.defineProperty(navigator,'webdriver',{get: ()=> undefined})"
   );
   ```
3. **Trusted IP Range**: Add `202.7.247.92` to Salesforce Network Access

**Alternative Approaches**:
- OAuth 2.0 API login (bypass UI completely)
- Frontdoor.jsp with session ID
- Manual login with pause for development
- Connected App for production automation

### Understanding Casting in Selenium
**Key Concept**: `ChromeDriver` implements multiple interfaces

```java
ChromeDriver implements WebDriver, JavascriptExecutor, TakesScreenshot, ...
```

**Why Cast Needed**:
```java
WebDriver driver = new ChromeDriver();  // Type is WebDriver interface
driver.executeScript(...);              // ❌ Error: WebDriver doesn't have this method
((JavascriptExecutor) driver).executeScript(...);  // ✅ Works: Cast to access method
```

**Analogy**: Person with multiple roles (Teacher, Driver). When introduced as "Teacher", you can't call "drive()" until you acknowledge they're also a "Driver".

**Practical Solution**: Utility classes (JavaScriptUtil, ActionsUtil) do casting once, so tests stay clean.

### Project Architecture Updates
**New Files**:
- `src/main/java/com/fsc/utils/JavaScriptUtil.java`
- `src/main/java/com/fsc/utils/ActionsUtil.java`
- `src/main/java/com/fsc/utils/ExtentReportManager.java`
- `src/test/java/com/fsc/listeners/TestListener.java`
- `docs/JAVASCRIPT_EXECUTOR_EXAMPLES.md`
- `docs/ACTIONS_CLASS_GUIDE.md`
- `docs/EXCEPTION_HANDLING_GUIDE.md`

**Dependencies Added**:
- Extent Reports 5.1.1

**BaseTest Enhanced**:
- Anti-detection ChromeOptions
- Webdriver flag hiding
- JavascriptExecutor import

## Session 4: First Test Implementation (January 16, 2026)

### SalesAppTest - Scenario 1 Complete
- Created first test without Page Objects (Option 1 approach)
- Implemented App Launcher navigation with manual verification pause
- **Key Challenge**: Finding stable locators in Salesforce Lightning
  - Avoid dynamic IDs (`input-212`) → Use `@placeholder`, `@data-label`, `@title`
  - App Launcher: `button[@title='App Launcher']`
  - Sales app: `a[@data-label='Sales']`

### Java Inheritance - extends BaseTest
- **Why needed**: BaseTest provides driver, setUp(), tearDown()
- **Not a snippet**: Object-oriented inheritance
- **Rule**: Always extend BaseTest, never extend other test classes
- Three reusability patterns: Page Objects (current), LoginHelper (production), Manual instantiation (wrong)

### Test Execution & Debugging
- `mvn test -Dtest=X` → Creates "Default suite" (ignores testng.xml)
- `mvn test` → Uses testng.xml, runs all tests in suite
- **Failure debugged**: TimeoutException - 40 seconds not enough for manual verification + page load
- Tests run: 7 total (4 login + 1 app launcher + 2 skipped)

### Quick Fixes
- `getText()` vs `getAttribute("title")` - title attribute more reliable in Lightning (text gets truncated)
- `getText()` takes **no parameters** (common error)
- `assertEquals(exact)` vs `assertTrue(contains)`

## Key Learnings Summary

### Session 1 (Jan 12): Foundation
✅ Java 17 setup, Maven project structure, TestNG configuration
✅ Page Object Model, GitHub repository
✅ Understanding dependencies and project organization

### Session 2 (Jan 13): Configuration & Best Practices
✅ ConfigReader for centralized config
✅ Explicit waits over Thread.sleep (critical!)
✅ ChromeOptions vs Window Handler
✅ Code reusability concepts

### Session 3 (Jan 15): Advanced Techniques & Reporting
✅ JavascriptExecutor for Salesforce challenges
✅ Actions class for complex interactions
✅ Exception handling strategies
✅ Extent Reports for test result tracking
✅ Salesforce automation detection bypass
✅ Understanding Java interfaces and casting

### Session 4 (Jan 16): Practical Test Development
✅ First complete test (SalesAppTest - Scenario 1)
✅ Stable locator strategies for Salesforce Lightning
✅ extends BaseTest inheritance pattern
✅ Test suite execution (testng.xml vs -Dtest)
✅ Debugging TimeoutException failures
✅ getText() vs getAttribute() differences

## To-Do List Progress

### Completed ✅
1. ✅ Install Maven
2. ✅ Create Maven project structure
3. ✅ Set up TestNG configuration
4. ✅ Implement Page Object Model
5. ✅ Create GitHub repository
6. ✅ Create ConfigReader utility
7. ✅ Add Salesforce credentials to config.properties
8. ✅ Create JavaScriptUtil for Salesforce-specific operations
9. ✅ Create ActionsUtil for complex user interactions
10. ✅ Set up Extent Reports for test reporting
11. ✅ Add anti-detection options for Salesforce automation
12. ✅ Create comprehensive documentation (5 guides)

### In Progress ⏳
13. ⏳ Configure Salesforce Trusted IP Range (attempted, still needs verification)
14. ✅ First test scenario completed (SalesAppTest)

### Future 📋
15. Refactor SalesAppTest to use Page Object Model
16. Complete remaining 5 practice scenarios
17. Add more page objects (Home, Accounts, Contacts, Opportunities)
18. Implement data-driven tests with TestNG DataProvider
19. Handle Shadow DOM elements in complex scenarios
20. Set up parallel test execution
21. Integrate with CI/CD pipeline

## Project Statistics

- **Files Created**: 18 (pages, tests, utils, listeners, docs, practice scenarios)
- **Lines of Code**: ~2,600+
- **Dependencies**: 6 (Selenium, TestNG, WebDriverManager, SLF4J, Extent Reports)
- **Test Cases**: 5 scenarios (4 login + 1 app launcher)
- **Utility Classes**: 4 (ConfigReader, JavaScriptUtil, ActionsUtil, ExtentReportManager)
- **Documentation Files**: 6 guides (Learning Notes, Quick Reference, JS Executor, Actions, Exception Handling, Practice Scenarios)
- **GitHub Commits**: 9+
- **Test Execution**: 7 tests run (4 pass, 1 fail, 2 skip)

---

*Updated: January 16, 2026*
