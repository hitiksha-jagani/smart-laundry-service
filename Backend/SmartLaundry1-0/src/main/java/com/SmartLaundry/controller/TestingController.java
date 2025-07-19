package com.SmartLaundry.controller;

import com.SmartLaundry.dto.DeliveryAgent.OrderAssignmentDTO;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.Payment;
import com.SmartLaundry.repository.PaymentRepository;
import com.SmartLaundry.service.Admin.PayoutAssignmentService;
import com.SmartLaundry.service.Customer.GeoUtils;
import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private PayoutAssignmentService payoutAssignmentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

    // @author Hitiksha Jagani
    // http://localhost:8080/testing/payout/{paymentId}
    // Test payout counting and storing
    @GetMapping("/payout/{paymentId}")
    public ResponseEntity<?> checkPayout(@PathVariable Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not exist."));
        payoutAssignmentService.addPayouts(payment);
        return ResponseEntity.ok("Payout counted.");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/testing/test-schedule
    // Test whether order reassigned or not
    @GetMapping("/test-schedule")
    public String testSchedule() throws JsonProcessingException {
        String redisKey = "assignment:" + "ODR00002";

        OrderAssignmentDTO assignment = new OrderAssignmentDTO("AGENT456", OrderStatus.PENDING, System.currentTimeMillis());

        String value = objectMapper.writeValueAsString(assignment);

        System.out.println("Writing JSON to Redis: " + value);
        redisTemplate.delete(redisKey);
        redisTemplate.opsForValue().setIfAbsent(redisKey, value);

//        deliveriesService.scheduleReassignment("ODR00002", "AGENT456");

        return "Reassignment scheduled!";
    }



}