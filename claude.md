# FSC Selenium Test Project - AI Assistant Guide

## Project Overview

This is a Selenium WebDriver automation framework for testing Salesforce applications, built with Java 17, Maven, and TestNG. The project follows the Page Object Model (POM) design pattern and includes automated driver management.

**Key Technologies:**
- Java 17
- Selenium WebDriver 4.17.0
- TestNG 7.9.0
- Maven (build tool)
- WebDriverManager 5.6.3 (automatic browser driver management)

## Project Architecture

### Design Patterns

1. **Page Object Model (POM)**: Each web page is represented as a Java class with locators and actions encapsulated
2. **Inheritance-based Test Framework**: All test classes extend `BaseTest` for common setup/teardown
3. **Explicit Waits**: Uses WebDriverWait for handling dynamic Salesforce elements

### Directory Structure

```
fsc-selenium-test/
├── pom.xml                                      # Maven project configuration
├── src/
│   ├── main/java/com/fsc/
│   │   └── pages/                               # Page Object Models
│   │       └── SalesforceLoginPage.java         # Login page POM
│   ├── test/java/com/fsc/
│   │   ├── base/
│   │   │   └── BaseTest.java                    # Base test setup/teardown
│   │   └── tests/
│   │       └── SalesforceLoginTest.java         # Login test cases
│   └── test/resources/
│       ├── testng.xml                           # TestNG suite configuration
│       └── config.properties                    # Environment configuration
└── target/                                       # Build output (generated)
```

## Core Components

### 1. BaseTest.java (`src/test/java/com/fsc/base/BaseTest.java`)

**Purpose**: Provides common WebDriver setup and teardown for all tests.

**Key Features:**
- Initializes ChromeDriver with WebDriverManager (automatic driver management)
- Configures Chrome options for Salesforce compatibility:
  - Maximized window
  - Disabled notifications
  - Disabled popup blocking
- Sets implicit wait: 10 seconds
- Sets page load timeout: 30 seconds
- Cleans up driver after each test method

**Usage Pattern:**
```java
public class YourTest extends BaseTest {
    // driver is available as protected field
    @Test
    public void testSomething() {
        // driver is already initialized
    }
}
```

### 2. SalesforceLoginPage.java (`src/main/java/com/fsc/pages/SalesforceLoginPage.java`)

**Purpose**: Page Object Model for Salesforce login page.

**Locators:**
- Username field: `By.id("username")`
- Password field: `By.id("password")`
- Login button: `By.id("Login")`
- Error message: `By.id("error")`

**Public Methods:**
- `navigateToLogin(String url)`: Navigate to login page
- `enterUsername(String username)`: Enter username with explicit wait
- `enterPassword(String password)`: Enter password with explicit wait
- `clickLoginButton()`: Click login button with explicit wait
- `login(String username, String password)`: Complete login flow
- `isErrorMessageDisplayed()`: Check if error message is visible
- `getErrorMessage()`: Get error message text

**Wait Strategy:**
- Uses explicit WebDriverWait with 15-second timeout
- Waits for element visibility before interaction
- Waits for clickability before clicking buttons

### 3. SalesforceLoginTest.java (`src/test/java/com/fsc/tests/SalesforceLoginTest.java`)

**Purpose**: Test suite for Salesforce login functionality.

**Test Cases:**
1. `testValidLogin`: Tests successful login with valid credentials (uses TestNG parameters)
2. `testInvalidLogin`: Tests login with invalid credentials, verifies error message
3. `testEmptyUsername`: Tests login with empty username field
4. `testEmptyPassword`: Tests login with empty password field

**Execution Order:**
Tests run in priority order (1-4) using TestNG `@Test(priority=N)`.

**Current Limitations:**
- `testValidLogin` has placeholder assertion (line 26) - needs customization for specific Salesforce instance
- Hardcoded URL: `https://login.salesforce.com` (line 12)

## Configuration Files

### testng.xml (`src/test/resources/testng.xml`)

**Purpose**: TestNG suite configuration.

**Current Configuration:**
- Suite name: "Salesforce Test Suite"
- Parallel execution: disabled (`parallel="false"`)
- Includes: `SalesforceLoginTest` class

**To add more tests:**
```xml
<classes>
    <class name="com.fsc.tests.SalesforceLoginTest"/>
    <class name="com.fsc.tests.YourNewTest"/>
</classes>
```

### config.properties (`src/test/resources/config.properties`)

**Purpose**: Centralized configuration for environment and test settings.

**Available Settings:**
- `salesforce.url`: Production login URL
- `salesforce.sandbox.url`: Sandbox login URL
- `salesforce.username`: Test username (NEVER commit real credentials!)
- `salesforce.password`: Test password (NEVER commit real credentials!)
- `browser`: Browser choice (currently chrome)
- `headless`: Run in headless mode (true/false)
- `implicit.wait`: Implicit wait timeout in seconds
- `explicit.wait`: Explicit wait timeout in seconds
- `page.load.timeout`: Page load timeout in seconds

**Note:** Currently not utilized by test code - needs integration.

## Running Tests

### Maven Commands

```bash
# Install dependencies
mvn clean install

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SalesforceLoginTest

# Run specific test method
mvn test -Dtest=SalesforceLoginTest#testInvalidLogin

# Run with TestNG XML
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Test Reports

TestNG generates HTML reports at:
- `target/surefire-reports/index.html`
- `test-output/` directory (TestNG default reports)

## Extending the Project

### Adding a New Page Object

1. Create a new class in `src/main/java/com/fsc/pages/`
2. Follow the pattern from `SalesforceLoginPage.java`:
   - Constructor accepts `WebDriver driver`
   - Initialize `WebDriverWait`
   - Define locators as `private By` fields
   - Create public methods for page actions
   - Use explicit waits before interactions

Example:
```java
public class SalesforceHomePage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By profileMenu = By.cssSelector(".profile-menu");

    public SalesforceHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickProfileMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(profileMenu)).click();
    }
}
```

### Adding a New Test Class

1. Create a new class in `src/test/java/com/fsc/tests/`
2. Extend `BaseTest`
3. Initialize page objects in `@BeforeMethod`
4. Write test methods with `@Test` annotation
5. Add to `testng.xml`

### Integrating config.properties

To use the configuration file, add a utility class:

```java
public class ConfigReader {
    private static Properties properties;

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
```

Then use in tests:
```java
String url = ConfigReader.getProperty("salesforce.url");
```

## Salesforce-Specific Considerations

### Dynamic Elements
- Salesforce Lightning uses dynamic IDs and class names
- Always use explicit waits for element visibility/clickability
- Prefer robust locators: CSS selectors with attributes, XPath with `contains()` or `starts-with()`

### Shadow DOM
Some Salesforce Lightning components use Shadow DOM:
```java
WebElement shadowHost = driver.findElement(By.cssSelector("selector"));
SearchContext shadowRoot = shadowHost.getShadowRoot();
WebElement element = shadowRoot.findElement(By.cssSelector("inner-selector"));
```

### iFrames
Navigate to frames when needed:
```java
driver.switchTo().frame("frameName");
// Interact with elements
driver.switchTo().defaultContent(); // Return to main content
```

### Security and MFA
- Salesforce may require Multi-Factor Authentication (MFA)
- Consider using OAuth tokens for automated testing
- Use sandbox environments for testing

## Dependencies (from pom.xml)

### Main Dependencies
- `selenium-java:4.17.0` - Selenium WebDriver
- `testng:7.9.0` - Testing framework
- `webdrivermanager:5.6.3` - Automatic driver management
- `slf4j-api:2.0.9` - Logging API
- `slf4j-simple:2.0.9` - Simple logging implementation

### Build Plugins
- `maven-compiler-plugin:3.11.0` - Java 17 compilation
- `maven-surefire-plugin:3.2.2` - Test execution

## Common Issues and Solutions

### ChromeDriver Version Mismatch
**Solution**: WebDriverManager handles this automatically. If issues persist, clear the driver cache:
```bash
rm -rf ~/.cache/selenium/
```

### Element Not Found
**Solution**: Increase explicit wait timeout or verify locator strategy.

### Salesforce Login Redirects
**Solution**: Update assertions to handle redirect URLs (e.g., `lightning.force.com`).

### Headless Mode
**Solution**: Add to ChromeOptions in BaseTest:
```java
options.addArguments("--headless=new");
```

## Next Steps / Improvements

1. **Integrate config.properties**: Create ConfigReader utility
2. **Complete testValidLogin assertion**: Add proper post-login verification
3. **Add more page objects**: Home page, Account page, Opportunity page
4. **Data-driven testing**: Use TestNG DataProvider for multiple test data sets
5. **Parallel execution**: Enable in testng.xml for faster test runs
6. **Screenshot capture**: Add on test failure
7. **Logging**: Implement proper logging with Log4j or SLF4J
8. **CI/CD integration**: Add GitHub Actions or Jenkins pipeline
9. **Extent Reports**: Add for better test reporting
10. **Cross-browser testing**: Add support for Firefox, Edge, Safari

## Code Style Guidelines

- Use descriptive method names (e.g., `clickLoginButton()` not `click()`)
- Keep page objects focused on single pages
- Use explicit waits, not Thread.sleep()
- Follow Java naming conventions (camelCase for methods/variables)
- Add JavaDoc comments for public methods
- Keep test methods independent and atomic
- Use meaningful assertions with custom messages

## Git Best Practices

- Never commit `config.properties` with real credentials
- Keep `.gitignore` updated for `target/`, `test-output/`, IDE files
- Use meaningful commit messages
- Create feature branches for new functionality
- Review changes before committing

## Support and Resources

- Selenium Documentation: https://www.selenium.dev/documentation/
- TestNG Documentation: https://testng.org/doc/documentation-main.html
- WebDriverManager: https://bonigarcia.dev/webdrivermanager/
- Salesforce Developer Docs: https://developer.salesforce.com/
