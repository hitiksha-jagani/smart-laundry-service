package com.SmartLaundry.controller;

import com.SmartLaundry.service.Customer.GeoUtils;
import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/testing")
public class TestingController {

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private GeoUtils geoUtils;

    private static final Logger logger = LoggerFactory.getLogger(TestingController.class);

    // @author Hitiksha Jagani
    // http://localhost:8080/testing/{orderId}
    // Just for testing
    @PostMapping("/{orderId}")
    public ResponseEntity<String> assignOrder(@PathVariable String orderId) {
        try {
            logger.info("Assigning order: {}", orderId);
            deliveriesService.assignToDeliveryAgentCustomerOrders(orderId);
            return ResponseEntity.ok("Assignment triggered for order: " + orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/testing/latlng
    // Just for testing to fetch latitude and longitude for given address
    @GetMapping("/latlng")
    public ResponseEntity<Map<String, Double>> getLatLng() {
        String fullAddress = String.format("%s, %s, %s, %s",
                "22, Dipak Colony, India Colony Road",
                "Bapunagar",
                "Ahmedabad",
                "380024"
        );

        double[] latLng = geoUtils.getLatLng(fullAddress);

        Map<String, Double> response = new HashMap<>();
        response.put("latitude", latLng[0]);
        response.put("longitude", latLng[1]);
        return ResponseEntity.ok(response);
    }

}