package com.SmartLaundry.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EnvUtils {

    public static void loadEnv(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.lines().forEach(line -> {
                line = line.trim();
                if (!line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        System.setProperty(key, value);
                        // For debugging:
                        System.out.println("Loaded env var: " + key + "=" + value);
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to load env file: " + filePath);
            e.printStackTrace();
        }
    }
}
