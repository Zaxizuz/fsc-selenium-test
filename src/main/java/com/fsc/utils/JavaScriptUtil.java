package com.fsc.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Utility class for JavaScript operations commonly needed in Salesforce testing
 */
public class JavaScriptUtil {
    private JavascriptExecutor js;

    public JavaScriptUtil(WebDriver driver) {
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Click element using JavaScript (useful when element is blocked by overlay)
     */
    public void clickElement(WebElement element) {
        js.executeScript("arguments[0].click();", element);
    }

    /**
     * Scroll element into view
     */
    public void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Scroll to bottom of page (useful for lazy loading)
     */
    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scroll to top of page
     */
    public void scrollToTop() {
        js.executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Set value in input field using JavaScript
     */
    public void setValue(WebElement element, String value) {
        js.executeScript("arguments[0].value='" + value + "';", element);
    }

    /**
     * Set value and trigger change event (important for Lightning components)
     */
    public void setValueAndTriggerChange(WebElement element, String value) {
        js.executeScript(
            "arguments[0].value='" + value + "';" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            element
        );
    }

    /**
     * Access Shadow DOM element
     */
    public WebElement getShadowRoot(WebElement shadowHost) {
        return (WebElement) js.executeScript("return arguments[0].shadowRoot", shadowHost);
    }

    /**
     * Highlight element with red border (for debugging)
     */
    public void highlightElement(WebElement element) {
        js.executeScript("arguments[0].style.border='3px solid red';", element);
    }

    /**
     * Remove highlight from element
     */
    public void removeHighlight(WebElement element) {
        js.executeScript("arguments[0].style.border='';", element);
    }

    /**
     * Check if element is really visible (has dimensions)
     */
    public boolean isElementVisible(WebElement element) {
        return (Boolean) js.executeScript(
            "var elem = arguments[0];" +
            "return elem.offsetWidth > 0 && elem.offsetHeight > 0;",
            element
        );
    }

    /**
     * Remove readonly attribute from element
     */
    public void removeReadOnly(WebElement element) {
        js.executeScript("arguments[0].removeAttribute('readonly');", element);
    }

    /**
     * Wait for page to fully load
     */
    public void waitForPageLoad() {
        js.executeScript("return document.readyState").equals("complete");
    }

    /**
     * Check if Lightning spinner is present
     */
    public boolean isSpinnerPresent() {
        return (Boolean) js.executeScript(
            "return document.querySelector('.slds-spinner') !== null"
        );
    }

    /**
     * Get text content (including hidden text)
     */
    public String getTextContent(WebElement element) {
        return (String) js.executeScript("return arguments[0].textContent;", element);
    }

    /**
     * Get computed CSS property value
     */
    public String getCSSValue(WebElement element, String property) {
        return (String) js.executeScript(
            "return window.getComputedStyle(arguments[0]).getPropertyValue('" + property + "');",
            element
        );
    }

    /**
     * Dispatch custom event on element
     */
    public void dispatchEvent(WebElement element, String eventType) {
        js.executeScript(
            "arguments[0].dispatchEvent(new Event('" + eventType + "', { bubbles: true }));",
            element
        );
    }

    /**
     * Flash element (highlight and unhighlight for visibility)
     */
    public void flashElement(WebElement element) {
        String originalStyle = element.getAttribute("style");
        for (int i = 0; i < 3; i++) {
            js.executeScript("arguments[0].style.border='3px solid red';", element);
            sleep(100);
            js.executeScript("arguments[0].style.border='';", element);
            sleep(100);
        }
        // Restore original style
        if (originalStyle != null) {
            js.executeScript("arguments[0].setAttribute('style', '" + originalStyle + "');", element);
        }
    }

    /**
     * Execute custom JavaScript
     */
    public Object executeScript(String script, Object... args) {
        return js.executeScript(script, args);
    }

    /**
     * Helper method for sleep
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
