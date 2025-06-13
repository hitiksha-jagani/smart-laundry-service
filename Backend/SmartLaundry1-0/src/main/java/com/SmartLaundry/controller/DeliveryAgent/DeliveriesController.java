package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliverySummaryResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderResponseDTO;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.OrderStatusHistory;
import com.SmartLaundry.model.Users;
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
    public ResponseEntity<DeliverySummaryResponseDTO> getDeliveriesSummary(HttpServletRequest request){
        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        DeliverySummaryResponseDTO deliverySummaryResponseDTO = deliveriesService.deliveriesSummary(agentId);
        return ResponseEntity.ok(deliverySummaryResponseDTO);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/today
    // Return total deliveries, pending deliveries and upcoming deliveries
    @GetMapping("/today")
    public ResponseEntity<List<OrderResponseDTO>> getTodaysDeliveries(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        List<OrderResponseDTO> orderResponseDTOS = deliveriesService.todaysDeliveries(user);
        return ResponseEntity.ok(orderResponseDTOS);
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
    public ResponseEntity<String> acceptedOrder(@PathVariable String orderId, HttpServletRequest request) throws AccessDeniedException {
        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(agentId);
        roleCheckingService.isDeliveryAgent(user);

        boolean result = deliveriesService.acceptOrder(orderId, agentId);

        if(result){
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not exist."));
            OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder()
                    .status(OrderStatus.ACCEPTED_BY_AGENT)
                    .order(order)
                    .build();
            return ResponseEntity.ok("Order accepted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Order already accepted or invalid agent.");
        }
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/deliveries/reject/{orderId}
    // Reject order
    @PostMapping("/reject/{orderId}")
    public ResponseEntity<String> rejectOrder(@PathVariable String orderId,  HttpServletRequest request) throws JsonProcessingException, AccessDeniedException {

        // Fetch agent id
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(agentId);
        roleCheckingService.isDeliveryAgent(user);

        // Add agent to rejected list in Redis
        redisTemplate.opsForSet().add("rejectedAgents:" + orderId, agentId);
        redisTemplate.expire("rejectedAgents:" + orderId, Duration.ofMinutes(30));

        // Remove current assignment to allow re-assignment
        redisTemplate.delete("assignment:" + orderId);

        // Logic to assign to next agent
        deliveriesService.assignToDeliveryAgent(orderId);

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

        return ResponseEntity.ok(deliveriesService.changeStatus(orderId));
    }

}
