package com.SmartLaundry.service.Customer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeoUtils {

    @Value("${OPEN_CAGE_API_KEY}")
    private String apiKey;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public double[] getLatLng(String address) {
        try {
            // Check Redis first
            String key = "geo:" + address.toLowerCase().trim().replaceAll("\\s+", "_");
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof double[]) {
                return (double[]) cached;
            }

            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://api.opencagedata.com/geocode/v1/json?q=" + encodedAddress + "&key=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("User", "SmartLaundryApp");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.has("results") && root.get("results").size() > 0) {
                JsonNode geometry = root.get("results").get(0).get("geometry");
                double lat = geometry.get("lat").asDouble();
                double lng = geometry.get("lng").asDouble();
                double[] coordinates = new double[]{lat, lng};

                // Cache the result in Redis
                redisTemplate.opsForValue().set(key, coordinates);
                return coordinates;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[]{0.0, 0.0}; // fallback if geocoding fails
    }
}

