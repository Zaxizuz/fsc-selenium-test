# Practice Test Scenarios for Selenium Learning

**Created**: January 15, 2026
**Purpose**: Hands-on scenarios to practice Selenium techniques with existing Salesforce functionality

---

## Part 1: General Salesforce Test Scenarios (3 Scenarios)

These scenarios test **existing Salesforce functionality** and help you practice core Selenium concepts.

---

### Scenario 1: Navigation and App Launcher Test

**Objective**: Practice element location, waits, and navigation

**Steps**:
1. Login to Salesforce
2. Click the App Launcher (waffle icon - 9 dots in top left)
3. Wait for the search box to appear
4. Type "Sales" in the search box
5. Click on the "Sales" app
6. Verify the URL contains "lightning"
7. Verify "Sales" text appears in the header

**Selenium Concepts to Practice**:
- ✅ Explicit waits (`visibilityOfElementLocated`)
- ✅ Element interaction (`click()`, `sendKeys()`)
- ✅ URL assertions
- ✅ Text verification

**Page Object to Create**:
```
AppLauncherPage.java
- clickAppLauncher()
- searchForApp(String appName)
- selectApp(String appName)
- verifyAppSelected(String appName)
```

**Locator Challenges**:
- App Launcher: Dynamic SLDS classes
- Search box: May be in Shadow DOM
- App tiles: Dynamic IDs

**Expected Test Code**:
```java
@Test
public void testNavigateToSalesApp() {
    // Login first
    AppLauncherPage appLauncher = new AppLauncherPage(driver);
    appLauncher.clickAppLauncher();
    appLauncher.searchForApp("Sales");
    appLauncher.selectApp("Sales");
    appLauncher.verifyAppSelected("Sales");
}
```

---

### Scenario 2: Create New Account Test

**Objective**: Practice form filling, dropdown selection, and verification

**Steps**:
1. Login and navigate to Accounts tab
2. Click "New" button
3. Fill in Account Name: "Test Account " + timestamp
4. Select Account Type from dropdown: "Customer - Direct"
5. Select Industry from dropdown: "Technology"
6. Fill in Phone: "0412345678"
7. Click "Save" button
8. Wait for success toast message
9. Verify you're on the Account detail page
10. Verify Account Name matches what you entered

**Selenium Concepts to Practice**:
- ✅ Handling Lightning combobox (dropdown)
- ✅ Form filling
- ✅ Dynamic waits for toast messages
- ✅ Timestamp generation for unique data
- ✅ Assertion of saved data
- ✅ Possible use of JavascriptExecutor for blocked clicks

**Page Objects to Create**:
```
AccountsListPage.java
- clickNew()

AccountCreatePage.java
- enterAccountName(String name)
- selectAccountType(String type)
- selectIndustry(String industry)
- enterPhone(String phone)
- clickSave()

AccountDetailPage.java
- getAccountName()
- verifySuccessToast()
```

**Locator Challenges**:
- "New" button: Multiple buttons on page
- Lightning combobox: Shadow DOM, dynamic IDs
- Toast message: Disappears quickly
- Save button: May be blocked by validation overlay

**Expected Test Code**:
```java
@Test
public void testCreateNewAccount() {
    String accountName = "Test Account " + System.currentTimeMillis();

    AccountsListPage accountsList = new AccountsListPage(driver);
    accountsList.navigateToAccounts();
    accountsList.clickNew();

    AccountCreatePage createPage = new AccountCreatePage(driver);
    createPage.enterAccountName(accountName);
    createPage.selectAccountType("Customer - Direct");
    createPage.selectIndustry("Technology");
    createPage.enterPhone("0412345678");
    createPage.clickSave();

    AccountDetailPage detailPage = new AccountDetailPage(driver);
    detailPage.verifySuccessToast();
    Assert.assertEquals(detailPage.getAccountName(), accountName);
}
```

---

### Scenario 3: List View Search and Filter Test

**Objective**: Practice searching, filtering, and table interaction

**Steps**:
1. Login and navigate to Accounts tab
2. Wait for the list view to load
3. Click the search box
4. Enter search term: "Burlington"
5. Wait for search results to filter
6. Verify at least one result contains "Burlington"
7. Click on the first result
8. Verify you're on the Account detail page
9. Verify the Account Name contains "Burlington"

**Selenium Concepts to Practice**:
- ✅ Search functionality
- ✅ Dynamic table handling
- ✅ Waiting for AJAX/search results
- ✅ XPath for table rows
- ✅ Text verification in tables

**Page Objects to Create**:
```
AccountsListPage.java
- searchAccounts(String searchTerm)
- getSearchResults()
- clickFirstResult()
- verifyResultContains(String text)
```

**Locator Challenges**:
- Search box: May be scoped to list view
- Table results: Dynamic rows, lazy loading
- First result link: Nested structure

**Expected Test Code**:
```java
@Test
public void testSearchAccountsByName() {
    AccountsListPage accountsList = new AccountsListPage(driver);
    accountsList.navigateToAccounts();
    accountsList.searchAccounts("Burlington");

    // Wait for results
    wait.until(driver -> accountsList.getSearchResults().size() > 0);

    accountsList.verifyResultContains("Burlington");
    accountsList.clickFirstResult();

    AccountDetailPage detailPage = new AccountDetailPage(driver);
    String accountName = detailPage.getAccountName();
    Assert.assertTrue(accountName.contains("Burlington"));
}
```

---

## Part 2: Financial Services Cloud Test Scenarios (3 Scenarios)

These scenarios test **existing FSC functionality** and help you learn FSC while practicing Selenium.

---

### Scenario 4: Household Creation Test (FSC-Specific)

**Objective**: Practice FSC Household object and relationship creation

**Background**: In FSC, Households group related individuals (family members, joint account holders)

**Steps**:
1. Login to Salesforce
2. Navigate to "Households" tab (via App Launcher if needed)
3. Click "New" button
4. Enter Household Name: "Test Family " + timestamp
5. Select Primary Contact from lookup (or create new)
6. Enter Household Phone: "0412345678"
7. Click "Save"
8. Verify Household detail page loads
9. Verify Household Name is correct
10. Verify Primary Contact is displayed

**Selenium Concepts to Practice**:
- ✅ FSC object navigation
- ✅ Lookup field handling (different from regular input)
- ✅ Relationship verification
- ✅ Custom object interaction

**FSC Concepts to Learn**:
- Household = Group of related people (FSC standard object)
- Primary Contact relationship
- Household-level information vs individual contact

**Page Objects to Create**:
```
HouseholdsListPage.java
- navigateToHouseholds()
- clickNew()

HouseholdCreatePage.java
- enterHouseholdName(String name)
- selectPrimaryContact(String contactName)
- enterPhone(String phone)
- clickSave()

HouseholdDetailPage.java
- getHouseholdName()
- getPrimaryContact()
```

**Locator Challenges**:
- Lookup field: Complex modal/dropdown
- FSC-specific layout: Different from standard Salesforce
- Related lists: Dynamic loading

---

### Scenario 5: Financial Account Creation Test (FSC-Specific)

**Objective**: Practice FSC Financial Account object (banking/investment accounts)

**Background**: In FSC, Financial Accounts represent bank accounts, investment accounts, loans, etc.

**Steps**:
1. Login and navigate to "Financial Accounts" tab
2. Click "New" button
3. Enter Account Name: "Savings Account " + timestamp
4. Select Financial Account Type: "Savings"
5. Select Primary Owner (Individual or Household lookup)
6. Enter Account Number: "12345678"
7. Enter Balance: "50000"
8. Click "Save"
9. Verify Financial Account detail page loads
10. Verify all entered data is correct
11. Navigate to related Primary Owner record
12. Verify Financial Account appears in Related List

**Selenium Concepts to Practice**:
- ✅ Dropdown/picklist selection
- ✅ Number field handling
- ✅ Lookup field interaction
- ✅ Related list verification
- ✅ Navigation between related records
- ✅ Possibly Actions class for hovering over related lists

**FSC Concepts to Learn**:
- Financial Account = Actual bank account/investment/loan (FSC standard object)
- Primary Owner relationship (can be Individual or Household)
- Account Types: Savings, Checking, Investment, Loan, Credit Card
- Balance tracking

**Page Objects to Create**:
```
FinancialAccountsListPage.java
- navigateToFinancialAccounts()
- clickNew()

FinancialAccountCreatePage.java
- enterAccountName(String name)
- selectAccountType(String type)
- selectPrimaryOwner(String ownerName)
- enterAccountNumber(String number)
- enterBalance(String amount)
- clickSave()

FinancialAccountDetailPage.java
- getAccountName()
- getAccountType()
- getBalance()
- navigateToPrimaryOwner()
```

**Locator Challenges**:
- FSC-specific fields and layouts
- Lookup modals
- Related list hover menus (may need Actions class)
- Currency fields (formatting)

---

### Scenario 6: Create Financial Goal with Tracking (FSC-Specific)

**Objective**: Practice FSC Financial Goal object and progress tracking

**Background**: In FSC, Financial Goals help track client objectives (retirement, home purchase, education)

**Steps**:
1. Login and navigate to "Financial Goals" tab
2. Click "New" button
3. Enter Goal Name: "Retirement Goal " + timestamp
4. Select Goal Type: "Retirement"
5. Select Individual/Household (Primary Owner)
6. Enter Target Value: "1000000"
7. Enter Current Value: "250000"
8. Select Target Date: 10 years from now
9. Select Status: "In Progress"
10. Click "Save"
11. Verify Financial Goal detail page loads
12. Verify Completion Percentage is calculated (25%)
13. Click "Edit" button
14. Update Current Value: "300000"
15. Click "Save"
16. Verify Completion Percentage updated (30%)

**Selenium Concepts to Practice**:
- ✅ Date picker interaction
- ✅ Number/currency field handling
- ✅ Calculated field verification
- ✅ Edit existing record flow
- ✅ Multiple save/verify cycles
- ✅ Possibly JavascriptExecutor for date picker

**FSC Concepts to Learn**:
- Financial Goal = Client's financial objective (FSC standard object)
- Goal Types: Retirement, Home Purchase, Education, Wealth Accumulation
- Target Value vs Current Value
- Automatic completion percentage calculation
- Goal tracking over time

**Page Objects to Create**:
```
FinancialGoalsListPage.java
- navigateToFinancialGoals()
- clickNew()

FinancialGoalCreatePage.java
- enterGoalName(String name)
- selectGoalType(String type)
- selectPrimaryOwner(String ownerName)
- enterTargetValue(String amount)
- enterCurrentValue(String amount)
- selectTargetDate(String date)
- selectStatus(String status)
- clickSave()

FinancialGoalDetailPage.java
- getGoalName()
- getCompletionPercentage()
- clickEdit()
- updateCurrentValue(String newAmount)
- verifyCompletionPercentage(String expected)
```

**Locator Challenges**:
- Date picker: Complex Lightning component (may need JavascriptExecutor)
- Currency fields: Formatting and validation
- Calculated fields: Read-only, need to wait for calculation
- Edit mode vs view mode: Different locators

---

## How to Approach These Scenarios

### 1. Start with Scenario 1 (Simplest)
- Practice basic navigation and waits
- Build confidence with Salesforce UI

### 2. Progress to Scenario 2 (Form Handling)
- Learn form filling techniques
- Practice dropdown/combobox handling
- Use JavascriptExecutor if needed

### 3. Try Scenario 3 (Search & Tables)
- Practice dynamic content handling
- Learn table interaction

### 4. Move to FSC Scenarios 4-6
- Learn FSC-specific objects
- Practice lookup relationships
- Understand FSC data model

---

## Tips for Success

### Before Writing Tests
1. **Manual exploration**: Navigate manually first, inspect elements
2. **Identify locators**: Use browser DevTools to find stable locators
3. **Plan page objects**: Decide which page classes you need

### During Test Development
1. **Start simple**: Get basic flow working first
2. **Add waits**: Add explicit waits as needed
3. **Handle exceptions**: Use try-catch during debugging
4. **Use utility classes**: JavaScriptUtil, ActionsUtil when standard methods fail

### After Test Creation
1. **Run multiple times**: Ensure stability
2. **Check Extent Report**: Verify logging works
3. **Refactor**: Clean up code, remove hardcoded values
4. **Document**: Add comments for tricky parts

---

## Locator Strategy Recommendations

### Prefer (in order):
1. **ID**: `By.id("username")` - Most stable
2. **CSS Selector**: `By.cssSelector(".slds-button")` - Fast and flexible
3. **XPath**: `By.xpath("//button[@title='New']")` - When CSS can't do it
4. **Link Text**: `By.linkText("Accounts")` - For links only

### Avoid:
- Absolute XPath: `/html/body/div[1]/div[2]...` - Breaks easily
- Class names alone: `By.className("button")` - Too generic

### Salesforce-Specific Tips:
- Use `contains()` for dynamic IDs: `//div[contains(@id, 'modal')]`
- Use `@title` attribute: `//button[@title='Save']`
- Use SLDS classes: `.slds-modal`, `.slds-button`

---

## Success Criteria

After completing these scenarios, you should be able to:

✅ Navigate Salesforce Lightning UI confidently
✅ Handle forms, dropdowns, and lookups
✅ Interact with tables and search results
✅ Use JavascriptExecutor when needed
✅ Use Actions class for hover interactions
✅ Understand FSC data model (Households, Financial Accounts, Goals)
✅ Create stable, maintainable page objects
✅ Write reliable tests with proper waits
✅ Generate Extent Reports for all tests

---

## Next Steps After Completing These Scenarios

1. **Data-Driven Testing**: Use TestNG DataProvider to run same test with different data
2. **Cross-Browser Testing**: Run tests on Firefox, Edge
3. **Parallel Execution**: Configure TestNG for parallel test runs
4. **API Integration**: Use Salesforce REST API for test data setup
5. **CI/CD**: Set up automated test runs with GitHub Actions

---

*Created: January 15, 2026*
