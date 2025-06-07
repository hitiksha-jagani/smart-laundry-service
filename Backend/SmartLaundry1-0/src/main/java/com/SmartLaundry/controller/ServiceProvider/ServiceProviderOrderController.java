package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.service.Customer.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-provider/orders")
@RequiredArgsConstructor
public class ServiceProviderOrderController {

    private final OrderService orderService;

    // Service Provider accepts the order
    @PostMapping("/accept/{userId}")
    public ResponseEntity<OrderResponseDto> acceptOrder(@PathVariable String userId) {
        var orderResponse = orderService.acceptOrder(userId);
        return ResponseEntity.ok(orderResponse);
    }

    // Service Provider rejects the order
    @PostMapping("/reject/{userId}")
    public ResponseEntity<Void> rejectOrder(@PathVariable String userId) {
        orderService.rejectOrder(userId);
        return ResponseEntity.ok().build();
    }
}
