package com.SmartLaundry.controller;

import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testing")
public class TestingController {

    @Autowired
    private DeliveriesService deliveriesService;

    private static final Logger logger = LoggerFactory.getLogger(TestingController.class);

    // @author Hitiksha Jagani
    // http://localhost:8080/testing/{orderId}
    // Just for testing
    @PostMapping("/{orderId}")
    public ResponseEntity<String> assignOrder(@PathVariable String orderId) {
        try {
            logger.info("Assigning order: {}", orderId);
            deliveriesService.assignToDeliveryAgent(orderId);
            return ResponseEntity.ok("Assignment triggered for order: " + orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
