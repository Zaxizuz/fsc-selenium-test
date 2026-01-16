package com.fsc.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fsc.utils.ExtentReportManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG Listener for Extent Reports
 * Automatically logs test results and captures screenshots on failure
 */
public class TestListener implements ITestListener {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        // Initialize Extent Reports once before all tests
        extent = ExtentReportManager.createInstance();
        System.out.println("=== Test Suite Started: " + context.getName() + " ===");
    }

    @Override
    public void onFinish(ITestContext context) {
        // Flush report after all tests complete
        ExtentReportManager.flush();
        System.out.println("=== Test Suite Finished: " + context.getName() + " ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Create ExtentTest for this test method
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        extentTest.set(test);

        System.out.println(">>> Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Mark test as passed
        extentTest.get().log(Status.PASS,
            MarkupHelper.createLabel("Test PASSED: " + result.getMethod().getMethodName(), ExtentColor.GREEN));

        System.out.println("✓ Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Mark test as failed
        extentTest.get().log(Status.FAIL,
            MarkupHelper.createLabel("Test FAILED: " + result.getMethod().getMethodName(), ExtentColor.RED));

        // Log the exception
        extentTest.get().fail(result.getThrowable());

        // Take screenshot if driver is available
        try {
            Object testClass = result.getInstance();
            WebDriver driver = (WebDriver) testClass.getClass().getField("driver").get(testClass);

            if (driver != null) {
                String screenshotPath = captureScreenshot(driver, result.getMethod().getMethodName());
                if (screenshotPath != null) {
                    extentTest.get().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                }
            }
        } catch (Exception e) {
            extentTest.get().log(Status.WARNING, "Could not capture screenshot: " + e.getMessage());
        }

        System.out.println("✗ Test Failed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Mark test as skipped
        extentTest.get().log(Status.SKIP,
            MarkupHelper.createLabel("Test SKIPPED: " + result.getMethod().getMethodName(), ExtentColor.YELLOW));

        if (result.getThrowable() != null) {
            extentTest.get().skip(result.getThrowable());
        }

        System.out.println("⊘ Test Skipped: " + result.getMethod().getMethodName());
    }

    /**
     * Capture screenshot and save to file
     */
    private String captureScreenshot(WebDriver driver, String testName) {
        try {
            // Create screenshots directory
            String screenshotsDir = System.getProperty("user.dir") + "/test-output/screenshots";
            File directory = new File(screenshotsDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate filename with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = screenshotsDir + "/" + fileName;

            // Take screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            // Copy to destination
            Path destination = Paths.get(filePath);
            Files.copy(source.toPath(), destination);

            System.out.println("Screenshot saved: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}
