package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.DeliveryAgent.OrderHistoryService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderHistoryController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OrderHistoryService orderHistoryService;

    // @author Hitiksha Jagani
    // http://localhost:8080/orders/completed
    // List of all completed deliveries
    @GetMapping("/completed")
    public ResponseEntity<List<Order>> getCompletedOrders(HttpServletRequest request){
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        List<Order> orders = orderHistoryService.completedOrders(userId);
        return ResponseEntity.ok(orders);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/cancelled

}
