package com.resumeanalyzer.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            System.err.println("Could not load config.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        // 1. Check environment variables
        String value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            return value;
        }

        // 2. Check system properties (e.g. -Dkey=value)
        value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            return value;
        }

        // 3. Check properties file
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
