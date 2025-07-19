package com.SmartLaundry.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConfigTest {

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    public RedisConfigTest(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        System.out.println("🚀 Redis Config Host: " + redisHost + " Port: " + redisPort);
        redisTemplate.opsForValue().set("testKey", "hello");
        System.out.println("✅ Redis set success: " + redisTemplate.opsForValue().get("testKey"));
    }
}

