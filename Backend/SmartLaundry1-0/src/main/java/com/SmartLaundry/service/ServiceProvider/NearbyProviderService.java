package com.SmartLaundry.service.ServiceProvider;
// Find nearby ServiceProviders
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NearbyProviderService {

    private final StringRedisTemplate redisTemplate;
    private final ServiceProviderRepository serviceProviderRepository;

    private static final String KEY = "service_providers_geo";

    public List<ServiceProvider> getProvidersNearby(Double latitude, Double longitude, double radiusKm) {
        Circle circle = new Circle(new Point(longitude, latitude),
                new Distance(radiusKm, Metrics.KILOMETERS));

        GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates();

        GeoResults<GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(KEY, circle, args);

        if (results == null || results.getContent().isEmpty()) {
            return List.of();
        }

        Set<String> userIds = results.getContent().stream()
                .map(result -> result.getContent().getName())
                .collect(Collectors.toSet());

        System.out.println("Geo Redis found userIds: " + userIds);

        // Fetch providers from DB
        List<ServiceProvider> providers = serviceProviderRepository.findByUser_UserIdIn(userIds);
        System.out.println("DB matched providers: " + providers.size());

        // ⛔ Remove Redis entries that no longer exist in DB
        Set<String> missingIds = userIds.stream()
                .filter(id -> providers.stream().noneMatch(p -> p.getUser().getUserId().equals(id)))
                .collect(Collectors.toSet());

        if (!missingIds.isEmpty()) {
            redisTemplate.opsForGeo().remove(KEY, missingIds.toArray(new String[0]));
            System.out.println("Removed stale Redis entries: " + missingIds);
        }


        return providers;
    }

}
