package com.SmartLaundry.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CustomEnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Map<String, Object> envVars = new HashMap<>();

        String envFile = "/app/Env_Var.env";

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            reader.lines().forEach(line -> {
                line = line.trim();
                if (!line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        envVars.put(key, value);
                        System.out.println("🔐 Loaded env var: " + key + "=" + value);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("❌ Failed to read .env file: " + e.getMessage());
        }

        MutablePropertySources sources = context.getEnvironment().getPropertySources();
        sources.addFirst(new MapPropertySource("customEnvVars", envVars));
    }
}
