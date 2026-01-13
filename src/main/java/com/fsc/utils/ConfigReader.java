package com.fsc.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to read configuration from config.properties file
 */
public class ConfigReader {
    private static Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";

    static {
        try {
            properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file: " + e.getMessage());
        }
    }

    public static String getSalesforceUrl() {
        return properties.getProperty("salesforce.url");
    }

    public static String getSalesforceSandboxUrl() {
        return properties.getProperty("salesforce.sandbox.url");
    }

    public static String getUsername() {
        return properties.getProperty("salesforce.username");
    }

    public static String getPassword() {
        return properties.getProperty("salesforce.password");
    }

    public static String getBrowser() {
        return properties.getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless"));
    }

    public static int getImplicitWait() {
        return Integer.parseInt(properties.getProperty("implicit.wait"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(properties.getProperty("explicit.wait"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(properties.getProperty("page.load.timeout"));
    }

    // Generic method to get any property
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
