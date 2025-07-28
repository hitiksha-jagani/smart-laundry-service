package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.DeliveryAgent.OrderHistoryService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderHistoryController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OrderHistoryService orderHistoryService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/orders/completed
    // List of all completed deliveries
    @GetMapping("/completed")
    public ResponseEntity<List<Order>> getCompletedOrders(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isDeliveryAgent(user);
        return ResponseEntity.ok(orderHistoryService.completedOrders(user));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/cancelled

}
