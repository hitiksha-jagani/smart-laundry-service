package com.SmartLaundry.service;

import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NearbyProviderService {

    private final StringRedisTemplate redisTemplate;
    private final ServiceProviderRepository serviceProviderRepository;

    private static final String KEY = "service_providers_geo";

    public List<ServiceProvider> getProvidersNearby(Double latitude, Double longitude, double radiusKm) {
        // Define the search area using Circle
        Circle circle = new Circle(new Point(longitude, latitude),
                new Distance(radiusKm, DistanceUnit.KILOMETERS));

        // Set up the radius command arguments to include coordinates
        GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates();

        // Fetch nearby geo results
        GeoResults<GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(KEY, circle, args);

        // Extract matching service provider IDs
        List<String> nearbyProviderIds = results.getContent().stream()
                .map(result -> result.getContent().getName())
                .toList();

        // Fetch corresponding service provider entities from DB
        return serviceProviderRepository.findAllById(nearbyProviderIds);
    }
}
