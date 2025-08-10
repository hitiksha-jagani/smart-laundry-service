package com.SmartLaundry;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DebugEnv implements CommandLineRunner {

    @Value("${DB_HOST:NOT_FOUND}")
    private String dbHost;

    @Value("${DB_PORT:NOT_FOUND}")
    private String dbPort;

    @Value("${DB_NAME:NOT_FOUND}")
    private String dbName;

//    @Value("${jwt.secret:NOT FOUND}")
//    private String secret;

    @Value("${custom.env.value}")
    private String envValue;

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @PostConstruct
    public void testEnv() {
        System.out.println("ENV VALUE = " + envValue);
    }

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Override
    public void run(String... args) {
        System.out.println("🔍 Listing all available Spring environment properties:");
        for (String key : new String[] {
                "DB_HOST", "DB_PORT", "DB_NAME", "jwt.secret",
                "paypal.client.id", "paypal.client.secret", "paypal.mode",
                "spring.redis.host", "spring.redis.port"
        }) {
            System.out.printf("→ %s = %s%n", key, environment.getProperty(key));
        }
    }

}
