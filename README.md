# FSC Selenium Test Project

Selenium automation testing project for Salesforce using Java, Maven, and TestNG.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Chrome browser installed

## Project Structure

```
fsc-selenium-test/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/java/
│   │   ├── com/fsc/base/
│   │   │   └── BaseTest.java        # Base test class with setup/teardown
│   │   └── com/fsc/pages/
│   │       └── SalesforceLoginPage.java  # Page Object Model for login
│   └── test/
│       ├── java/
│       │   └── com/fsc/tests/
│       │       └── SalesforceLoginTest.java  # Test cases
│       └── resources/
│           ├── testng.xml           # TestNG suite configuration
│           └── config.properties    # Configuration file
└── README.md
```

## Setup Instructions

1. **Clone/Navigate to the project**
   ```bash
   cd fsc-selenium-test
   ```

2. **Update credentials**
   - Edit `src/test/resources/config.properties`
   - Add your Salesforce credentials
   - **NEVER** commit real credentials to version control!

3. **Install dependencies**
   ```bash
   mvn clean install
   ```

## Running Tests

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=SalesforceLoginTest
```

### Run specific test method
```bash
mvn test -Dtest=SalesforceLoginTest#testValidLogin
```

### Run with TestNG XML
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

## Key Features

- **Page Object Model**: Organized page classes for maintainability
- **TestNG Framework**: Advanced test configuration and reporting
- **WebDriverManager**: Automatic browser driver management
- **Explicit Waits**: Handles Salesforce's dynamic elements
- **Configuration File**: Centralized settings management

## Salesforce-Specific Considerations

1. **Dynamic Elements**: Salesforce Lightning UI has dynamic IDs
   - Use robust locators (CSS, XPath with contains/starts-with)
   - Use explicit waits for element visibility

2. **Shadow DOM**: Some elements are in Shadow DOM
   - May require JavaScript execution to access

3. **iFrames**: Navigate to correct frame before interacting
   ```java
   driver.switchTo().frame("frameName");
   ```

## Test Reports

After running tests, TestNG generates HTML reports at:
```
target/surefire-reports/index.html
```

## Next Steps

1. Update `config.properties` with your Salesforce URL and credentials
2. Customize the test cases based on your requirements
3. Add more Page Object Models for other Salesforce pages
4. Implement data-driven testing with TestNG DataProviders

## Troubleshooting

- **ChromeDriver issues**: WebDriverManager should auto-download the correct driver
- **Element not found**: Increase wait times in config.properties
- **Login fails**: Verify your Salesforce URL (login.salesforce.com vs test.salesforce.com for sandbox)
