package com.SmartLaundry.service.Customer;

import com.SmartLaundry.service.Admin.SettingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class GeoUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SettingService settingService;

    private static final Logger logger = LoggerFactory.getLogger(GeoUtils.class);
    private static final Duration REDIS_TTL = Duration.ofDays(7); // cache for 7 days

    public double[] getLatLng(String address) {

        if (address == null || address.isBlank()) {
            logger.warn("Empty or null address passed to geocoder.");
            return new double[]{0.0, 0.0};
        }

        try {
            String key = "geo:" + address.toLowerCase().trim().replaceAll("\\s+", "_");
            logger.info("Redis key for geolocation : {}", key);
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof double[]) {
                logger.info("Returning cached coordinates for address: {}", address);
                return (double[]) cached;
            }

            String apiProvider = settingService.getCurrentProvider();
            String apiKey = settingService.getCurrentApiKey();

            logger.info("Using provider: {}, with key: {}", apiProvider, apiKey != null ? "provided" : "missing");

            double[] coordinates;

            switch (apiProvider.toLowerCase()) {
                case "google":
                    coordinates = fetchFromGoogle(address, apiKey);
                    break;
                case "opencage":
                    coordinates = fetchFromOpenCage(address, apiKey);
                    break;
                default:
                    logger.warn("Unsupported geocoding provider: {}", apiProvider);
                    return new double[]{0.0, 0.0};
            }

            if (coordinates != null && coordinates[0] != 0.0 && coordinates[1] != 0.0) {
                redisTemplate.opsForValue().set(key, coordinates, REDIS_TTL);
                return coordinates;
            } else {
                logger.warn("Skipping cache/storage for invalid coordinates");
                return new double[]{0.0, 0.0};
            }

        } catch (Exception e) {
            logger.error("Geocoding failed for address '{}': {}", address, e.getMessage(), e);
            return new double[]{0.0, 0.0};
        }
    }

    private double[] fetchFromGoogle(String address, String apiKey) throws IOException {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
//                .queryParam("address", URLEncoder.encode(address, StandardCharsets.UTF_8))
                .queryParam("q", address)
                .queryParam("key", apiKey)
                .toUriString();

        return extractCoordinates(url, "/results/0/geometry/location");
    }

    private double[] fetchFromOpenCage(String address, String apiKey) throws IOException {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.opencagedata.com/geocode/v1/json")
//                .queryParam("q", URLEncoder.encode(address, StandardCharsets.UTF_8))
                .queryParam("q", address)
                .queryParam("key", apiKey)
                .toUriString();

        return extractCoordinates(url, "/results/0/geometry");
    }

    private double[] extractCoordinates(String url, String jsonPath) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        logger.debug("Full OpenCage Response: {}", response.getBody());

        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Geocoding API failed with status: {}", response.getStatusCode());
            return new double[]{0.0, 0.0};
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode locationNode = root.at(jsonPath);
        logger.info("Extracted locationNode at {}: {}", jsonPath, locationNode);

        if (locationNode == null || locationNode.isMissingNode() || !locationNode.isObject()) {
            logger.error("Invalid or missing geometry object in response at path: {}", jsonPath);
            return null;
        }

        JsonNode latNode = locationNode.get("lat");
        JsonNode lngNode = locationNode.get("lng");

        if (latNode == null || lngNode == null || !latNode.isNumber() || !lngNode.isNumber()) {
            logger.error("Latitude or Longitude missing or invalid in response node: {}", locationNode.toString());
            return null;
        }

        double[] coordinates = new double[]{latNode.asDouble(), lngNode.asDouble()};
        logger.info("Parsed coordinates: {}", coordinates);
        return coordinates;
    }

}

