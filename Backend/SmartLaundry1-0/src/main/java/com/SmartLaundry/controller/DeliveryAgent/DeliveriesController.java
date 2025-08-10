package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliverySummaryResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PendingDeliveriesResponseDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deliveries")
public class DeliveriesController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private DeliveriesService deliveriesService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RoleCheckingService roleCheckingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/summary
    // Return total deliveries, pending deliveries and upcoming deliveries
    @GetMapping("/summary")
    public ResponseEntity<?> getDeliveriesSummary(HttpServletRequest request) throws AccessDeniedException {
        // Fetch agent id
        try {
            String token = jwtService.extractTokenFromHeader(request);
            String agentId = (String) jwtService.extractUserId(token);
            Users user = roleCheckingService.checkUser(agentId);
            roleCheckingService.isDeliveryAgent(user);

            DeliverySummaryResponseDTO summary = deliveriesService.deliveriesSummary(agentId);

            return ResponseEntity.ok(summary);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch summary", "message", ex.getMessage()));
        }

    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/pending
    // Return a list of pending deliveries
    @GetMapping("/pending")
    public ResponseEntity<List<PendingDeliveriesResponseDTO>> getPendingDeliveries(HttpServletRequest request) throws AccessDeniedException, JsonProcessingException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(deliveriesService.pendingDeliveries(user));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/today
    // Return a list of upcoming deliveries
    @GetMapping("/today")
    public ResponseEntity<List<PendingDeliveriesResponseDTO>> getTodayDeliveries(HttpServletRequest request) throws AccessDeniedException {
        // Fetch agent id
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(deliveriesService.getTodayDeliveries(user));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/accept/{orderId}
    // Accept order
    @PostMapping("/accept/{orderId}")
    public ResponseEntity<String> acceptedOrder(@PathVariable String orderId, HttpServletRequest request) throws AccessDeniedException {
        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(agentId);
        roleCheckingService.isDeliveryAgent(user);
        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }
        deliveriesService.acceptOrder(orderId, agentId);

        // Remove all rejected agents
        redisTemplate.delete("rejectedAgents:" + orderId);
        return ResponseEntity.ok("Order accepted successfully.");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/reject/{orderId}
    // Reject order
    @PostMapping("/reject/{orderId}")
    public ResponseEntity<String> rejectOrder(@PathVariable String orderId,  HttpServletRequest request) throws JsonProcessingException, AccessDeniedException {

        // Fetch agent id
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);

        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }

        // Add agent to rejected list in Redis
        redisTemplate.opsForSet().add("rejectedAgents:" + orderId, userId);
        redisTemplate.expire("rejectedAgents:" + orderId, Duration.ofMinutes(30));

        Order order = orderRepository.findById(orderId).orElse(null);

        RejectedOrders rejectedOrders = RejectedOrders.builder()
                .order(order)
                .users(user)
                .build();

        // Remove current assignment to allow re-assignment
        redisTemplate.delete("assignment:" + orderId);

        // Logic to assign to next agent
        deliveriesService.assignToDeliveryAgentCustomerOrders(orderId);

        return ResponseEntity.ok("Order rejected successfully.");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/today/{orderId}
    // Return form for update status of order by entering otp
    @PutMapping("/today/{orderId}")
    public ResponseEntity<String> changeStatus(HttpServletRequest request, @PathVariable String orderId) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);

        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }

        return ResponseEntity.ok(deliveriesService.changeStatus(orderId));
    }

}