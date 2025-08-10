package com.SmartLaundry.controller;

import com.SmartLaundry.service.Customer.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearServiceProviderCache() {
        cacheService.clearServiceProvidersCache();
        return ResponseEntity.ok("Service Provider cache cleared successfully!");
    }
}

