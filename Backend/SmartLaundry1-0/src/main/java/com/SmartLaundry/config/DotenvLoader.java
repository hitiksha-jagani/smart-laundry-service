package com.SmartLaundry.config;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class DotenvLoader {
    public static void load(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                String trimmed = line.trim();
                if (!trimmed.startsWith("#") && trimmed.contains("=")) {
                    String[] parts = trimmed.split("=", 2);
                    if (parts.length == 2) {
                        System.setProperty(parts[0], parts[1]);
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Could not load .env file: " + e.getMessage());
        }
    }
}

