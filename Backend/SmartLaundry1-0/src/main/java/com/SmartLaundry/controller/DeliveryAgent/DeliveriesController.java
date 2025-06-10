package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliverySummaryResponseDTO;
import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/deliveries")
public class DeliveriesController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/summary
    // Return total deliveries, pending deliveries and upcoming deliveries
    @GetMapping("/summary")
    public ResponseEntity<DeliverySummaryResponseDTO> getDeliveriesSummary(HttpServletRequest request){
        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        DeliverySummaryResponseDTO deliverySummaryResponseDTO = deliveriesService.deliveriesSummary(agentId);
        return ResponseEntity.ok(deliverySummaryResponseDTO);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/pending
    // Return a list of pending deliveries
//    @GetMapping("/pending")
//    public ResponseEntity<Order> getDeliveriesSummary(HttpServletRequest request){
//        // Fetch agent id
//        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
//        DeliverySummaryResponseDTO deliverySummaryResponseDTO = deliveriesService.deliveriesSummary(agentId);
//        return ResponseEntity.ok(deliverySummaryResponseDTO);
//    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/upcoming
    // Return a list of upcoming deliveries
//    @GetMapping("/upcoming")
//    public ResponseEntity<DeliverySummaryResponseDTO> getDeliveriesSummary(HttpServletRequest request){
//        // Fetch agent id
//        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
//        DeliverySummaryResponseDTO deliverySummaryResponseDTO = deliveriesService.deliveriesSummary(agentId);
//        return ResponseEntity.ok(deliverySummaryResponseDTO);
//    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/accept/{orderId}
    // Accept order
    @PostMapping("/accept/{orderId}")
    public ResponseEntity<String> acceptedOrder(@PathVariable String orderId, HttpServletRequest request){
        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        boolean result = deliveriesService.acceptOrder(orderId, agentId);

        if(result){
            return ResponseEntity.ok("Order accepted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Order already accepted or invalid agent.");
        }
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/reject/{orderId}
    // Reject order
    @PostMapping("/reject/{orderId}")
    public ResponseEntity<?> rejectOrder(@PathVariable String orderId,  HttpServletRequest request) throws JsonProcessingException {

        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        // Add agent to rejected list in Redis
        redisTemplate.opsForSet().add("rejectedAgents:" + orderId, agentId);
        redisTemplate.expire("rejectedAgents:" + orderId, Duration.ofMinutes(10));

        // Logic to assign to next agent
        deliveriesService.assignToDeliveryAgent(orderId);

        return ResponseEntity.ok("Order rejected successfully.");
    }


}
