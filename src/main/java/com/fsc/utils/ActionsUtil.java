package com.fsc.utils;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

/**
 * Utility class for Actions operations commonly needed in Salesforce testing
 * Wraps org.openqa.selenium.interactions.Actions for convenience
 */
public class ActionsUtil {
    private Actions actions;

    public ActionsUtil(WebDriver driver) {
        this.actions = new Actions(driver);
    }

    /**
     * Hover over an element (move mouse to element)
     * Common use: Salesforce menus that appear on hover
     */
    public void hoverOver(WebElement element) {
        actions.moveToElement(element).perform();
    }

    /**
     * Hover over first element, then click second element
     * Common use: Dropdown menus, nested navigation
     */
    public void hoverAndClick(WebElement hoverElement, WebElement clickElement) {
        actions.moveToElement(hoverElement)
               .pause(Duration.ofMillis(500))
               .moveToElement(clickElement)
               .click()
               .perform();
    }

    /**
     * Right click on element (context menu)
     */
    public void rightClick(WebElement element) {
        actions.contextClick(element).perform();
    }

    /**
     * Double click on element
     * Common use: Edit inline in Salesforce tables
     */
    public void doubleClick(WebElement element) {
        actions.doubleClick(element).perform();
    }

    /**
     * Drag and drop from source to target element
     * Common use: Salesforce customization, reordering lists
     */
    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
    }

    /**
     * Drag element and drop at offset
     * @param element Element to drag
     * @param xOffset Horizontal offset in pixels
     * @param yOffset Vertical offset in pixels
     */
    public void dragAndDropByOffset(WebElement element, int xOffset, int yOffset) {
        actions.clickAndHold(element)
               .moveByOffset(xOffset, yOffset)
               .release()
               .perform();
    }

    /**
     * Send keys (keyboard input)
     */
    public void sendKeys(CharSequence... keys) {
        actions.sendKeys(keys).perform();
    }

    /**
     * Click element and send keys
     */
    public void clickAndType(WebElement element, String text) {
        actions.moveToElement(element)
               .click()
               .sendKeys(text)
               .perform();
    }

    /**
     * Press Tab key (navigate to next field)
     */
    public void pressTab() {
        actions.sendKeys(Keys.TAB).perform();
    }

    /**
     * Press Enter key
     */
    public void pressEnter() {
        actions.sendKeys(Keys.ENTER).perform();
    }

    /**
     * Press Escape key
     */
    public void pressEscape() {
        actions.sendKeys(Keys.ESCAPE).perform();
    }

    /**
     * Select all (Ctrl+A on Windows/Linux, Cmd+A on Mac)
     */
    public void selectAll() {
        String os = System.getProperty("os.name").toLowerCase();
        Keys modifier = os.contains("mac") ? Keys.COMMAND : Keys.CONTROL;

        actions.keyDown(modifier)
               .sendKeys("a")
               .keyUp(modifier)
               .perform();
    }

    /**
     * Copy (Ctrl+C on Windows/Linux, Cmd+C on Mac)
     */
    public void copy() {
        String os = System.getProperty("os.name").toLowerCase();
        Keys modifier = os.contains("mac") ? Keys.COMMAND : Keys.CONTROL;

        actions.keyDown(modifier)
               .sendKeys("c")
               .keyUp(modifier)
               .perform();
    }

    /**
     * Paste (Ctrl+V on Windows/Linux, Cmd+V on Mac)
     */
    public void paste() {
        String os = System.getProperty("os.name").toLowerCase();
        Keys modifier = os.contains("mac") ? Keys.COMMAND : Keys.CONTROL;

        actions.keyDown(modifier)
               .sendKeys("v")
               .keyUp(modifier)
               .perform();
    }

    /**
     * Scroll to element (Selenium 4+)
     */
    public void scrollToElement(WebElement element) {
        actions.scrollToElement(element).perform();
    }

    /**
     * Scroll by amount
     * @param deltaX Horizontal scroll (positive = right, negative = left)
     * @param deltaY Vertical scroll (positive = down, negative = up)
     */
    public void scrollByAmount(int deltaX, int deltaY) {
        actions.scrollByAmount(deltaX, deltaY).perform();
    }

    /**
     * Move mouse to element with offset
     */
    public void moveToElementWithOffset(WebElement element, int xOffset, int yOffset) {
        actions.moveToElement(element, xOffset, yOffset).perform();
    }

    /**
     * Click and hold (for drag operations)
     */
    public void clickAndHold(WebElement element) {
        actions.clickAndHold(element).perform();
    }

    /**
     * Release mouse button
     */
    public void release() {
        actions.release().perform();
    }

    /**
     * Pause for specified duration
     * Use sparingly - prefer explicit waits when possible
     */
    public void pause(long milliseconds) {
        actions.pause(Duration.ofMillis(milliseconds)).perform();
    }

    /**
     * Get the underlying Actions object for custom operations
     */
    public Actions getActions() {
        return actions;
    }
}
