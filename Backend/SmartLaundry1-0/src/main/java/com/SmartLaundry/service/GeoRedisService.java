package com.SmartLaundry.service;

import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class GeoRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ServiceProviderRepository serviceProviderRepository;

    private static final String KEY = "service_providers_geo";


    @PostConstruct
    public void init() {
        System.out.println("🔍 Redis Client Info: " + redisTemplate.getConnectionFactory().getConnection().getNativeConnection());
        List<ServiceProvider> providers = serviceProviderRepository.findAll();
        loadProviderGeoData(providers);
    }

    public void loadProviderGeoData(List<ServiceProvider> providers) {
        // Clear existing data
        redisTemplate.delete(KEY);

        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        int count = 0;

        for (ServiceProvider sp : providers) {
            if (sp.getUser() != null && sp.getUser().getAddress() != null) {
                UserAddress address = sp.getUser().getAddress();

                if (address.getLatitude() != null && address.getLongitude() != null) {
                    geoOps.add(
                            KEY,
                            new RedisGeoCommands.GeoLocation<>(
                                    sp.getUser().getUserId(), // Use user ID as Redis key
                                    new Point(address.getLongitude(), address.getLatitude()) // Redis expects (lng, lat)
                            )
                    );
                    count++;
                }
            }
        }

        System.out.println("✅ Loaded " + count + " service providers into Redis geo set.");
    }


}
