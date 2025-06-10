package com.SmartLaundry.service.Customer;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearServiceProvidersCache() {
        cacheManager.getCache("serviceProvidersCache").clear();
    }
}

