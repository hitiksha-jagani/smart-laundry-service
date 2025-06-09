package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.controller.ExtractHeader;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provider/orders")
@RequiredArgsConstructor
public class ServiceProviderOrderController {

    private final ServiceProviderOrderService serviceProviderOrderService;
    private final JWTService jwtService;
    private final ExtractHeader extractHeader;
    private final OrderService orderService;
    // Extract userId from JWT token
    private String getServiceProviderUserId(HttpServletRequest request) {
        String token = extractHeader.extractTokenFromHeader(request);
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Authorization token missing or empty");
        }
        Object userIdClaim = jwtService.extractClaim(token, claims -> claims.get("id"));
        if (userIdClaim == null) {
            throw new RuntimeException("User ID not found in token");
        }
        return userIdClaim.toString();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponseDto>> getPendingOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        List<OrderResponseDto> pendingOrders = serviceProviderOrderService.getPendingOrdersForServiceProvider(spUserId);
        return ResponseEntity.ok(pendingOrders);
    }

    @PostMapping("/{customerUserId}/accept")
    public ResponseEntity<OrderResponseDto> acceptOrder(HttpServletRequest request,
                                                        @PathVariable String customerUserId) {
        String spUserId = getServiceProviderUserId(request);

        OrderResponseDto response = serviceProviderOrderService.acceptOrder(spUserId, customerUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{customerUserId}/reject")
    public ResponseEntity<String> rejectOrder(HttpServletRequest request,
                                              @PathVariable String customerUserId) {
        String spUserId = getServiceProviderUserId(request);
        serviceProviderOrderService.rejectOrder(spUserId, customerUserId);
        return ResponseEntity.ok("Order rejected successfully.");
    }
}
