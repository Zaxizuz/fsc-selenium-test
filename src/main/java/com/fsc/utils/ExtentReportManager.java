package com.fsc.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages Extent Reports lifecycle
 * Creates HTML test reports with screenshots
 */
public class ExtentReportManager {
    private static ExtentReports extent;
    private static String reportPath;

    /**
     * Initialize Extent Reports
     * Call this once before all tests
     */
    public static ExtentReports createInstance() {
        if (extent == null) {
            // Create reports directory
            String reportsDir = System.getProperty("user.dir") + "/test-output/extent-reports";
            File directory = new File(reportsDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate report filename with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportPath = reportsDir + "/TestReport_" + timestamp + ".html";

            // Create Spark reporter (HTML report)
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            // Configure report
            sparkReporter.config().setDocumentTitle("Salesforce Automation Test Report");
            sparkReporter.config().setReportName("FSC Selenium Test Results");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            // Initialize ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Add system information
            extent.setSystemInfo("Application", "Salesforce");
            extent.setSystemInfo("Environment", ConfigReader.getSalesforceUrl());
            extent.setSystemInfo("Browser", ConfigReader.getBrowser());
            extent.setSystemInfo("Headless Mode", String.valueOf(ConfigReader.isHeadless()));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }

        return extent;
    }

    /**
     * Get ExtentReports instance
     */
    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    /**
     * Flush report (write to file)
     * Call this after all tests complete
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("Extent Report generated: " + reportPath);
        }
    }

    /**
     * Get report path
     */
    public static String getReportPath() {
        return reportPath;
    }
}
