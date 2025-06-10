package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class DeliveryAgentService {

    private static final String REDIS_KEY_PREFIX = "deliveryAgentLocation:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    // @author Hitiksha Jagani
    public void updateLocation(String agentId, Double latitude, Double longitude) {
        String key = REDIS_KEY_PREFIX + agentId;

        // Store a simple map or a custom object in Redis
        Map<String, Double> locationMap = new HashMap<>();
        locationMap.put("latitude", latitude);
        locationMap.put("longitude", longitude);

        redisTemplate.opsForValue().set(key, locationMap);
        System.out.println("Saved location for: " + key);

        // Optionally, set TTL for automatic expiration if agent goes offline
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    // @author Hitiksha Jagani
    // Periodically (e.g., scheduled every 5 minutes), save location from Redis to DB
    @Scheduled(fixedRate = 300000) // every 5 minutes
    @Transactional
    public void persistLocationsFromCache() {
        // Get all keys matching prefix
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            String userId = key.replace(REDIS_KEY_PREFIX, "");
            Map<String, Double> location = (Map<String, Double>) redisTemplate.opsForValue().get(key);
            if (location == null) continue;

            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Delivery agent not found for user: " + userId));

            agent.setCurrentLatitude(location.get("latitude"));
            agent.setCurrentLongitude(location.get("longitude"));
            deliveryAgentRepository.save(agent);
        }
    }
}

