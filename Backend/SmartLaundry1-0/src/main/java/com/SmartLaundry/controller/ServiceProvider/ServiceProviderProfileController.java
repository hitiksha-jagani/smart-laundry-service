package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ServiceProviderProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-provider/")
@RequiredArgsConstructor
public class ServiceProviderProfileController {

    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("submit-profile/{userId}")
    public ResponseEntity<String> submitProfile(
            @PathVariable String userId,
            @RequestBody ServiceProviderProfileDTO profileDTO) {

        // Save profile data in Redis with key prefix + userId
        redisTemplate.opsForValue().set("serviceProviderProfile:" + userId, profileDTO);

        return ResponseEntity.ok("Profile data saved and pending admin approval");
    }
}

