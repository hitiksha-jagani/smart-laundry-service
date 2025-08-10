package com.SmartLaundry.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public LatLng getLatLongFromAddress(String address) {
        // Append ', India' as default country for Indian services
        String fullAddress = address + ", India";

        // Use OpenStreetMap Nominatim for free geocoding
        String url = UriComponentsBuilder.fromHttpUrl("https://nominatim.openstreetmap.org/search")
                .queryParam("q", fullAddress)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .toUriString();

        try {
            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);
            if (response != null && response.length > 0) {
                double lat = Double.parseDouble(response[0].getLat());
                double lon = Double.parseDouble(response[0].getLon());
                return new LatLng(lat, lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new LatLng(0.0, 0.0); // fallback default
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class NominatimResponse {
        private String lat;
        private String lon;
    }

    @Data
    @AllArgsConstructor
    public static class LatLng {
        private double latitude;
        private double longitude;
    }
}
