package com.SmartLaundry.service;

import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.UserAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoRedisService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "service_providers_geo";

    public void loadProviderGeoData(List<ServiceProvider> providers) {
        // Clear old geo data before loading fresh data
        redisTemplate.delete(KEY);

        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
        int count = 0;

        for (ServiceProvider sp : providers) {
            if (sp.getUser() != null && sp.getUser().getAddress() != null && !sp.getUser().getAddress().isEmpty()) {
                UserAddress address = sp.getUser().getAddress().get(0);

                if (address.getLatitude() != null && address.getLongitude() != null) {
                    geoOps.add(
                            KEY,
                            new RedisGeoCommands.GeoLocation<>(
                                    sp.getUser().getUserId(),  // Use userId as member name
                                    new Point(address.getLongitude(), address.getLatitude())
                            )
                    );
                    count++;
                }
            }
        }

        System.out.println("Loaded " + count + " service providers into Redis geo set.");
    }


}



