package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.Customer.*;
import com.SmartLaundry.model.BookingItem;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.service.Customer.OrderBookingService;
import com.SmartLaundry.service.Customer.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderBookingService orderBookingService;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;


    private String redisKey(String userId) {
        return "order:user:" + userId;
    }

    @PostMapping("/{userId}/schedule-plan-flag")
    public ResponseEntity<?> saveGoWithSchedulePlanFlag(
            @PathVariable String userId,
            @RequestParam boolean goWithSchedulePlan) {

        redisTemplate.opsForHash().put(redisKey(userId), "goWithSchedulePlan", goWithSchedulePlan);
        return ResponseEntity.ok("goWithSchedulePlan flag saved.");
    }

    @PostMapping("/{userId}/booking-items")
    public ResponseEntity<?> saveBookingItems(
            @PathVariable String userId,
            @RequestBody @Valid List<OrderItemRequest> bookingItems) {

        try {
            String itemsJson = objectMapper.writeValueAsString(bookingItems);
            redisTemplate.opsForHash().put(redisKey(userId), "items", itemsJson);
            return ResponseEntity.ok("Booking items saved in Redis.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Failed to serialize booking items.");
        }
    }

    @PostMapping("/{userId}/initial-details")
    public ResponseEntity<?> saveInitialOrderDetails(@PathVariable String userId, @RequestBody BookOrderRequestDto dto) {
        orderService.saveInitialOrderDetails(userId, dto);
        return ResponseEntity.ok("Initial order details saved.");
    }

    @PostMapping("/{userId}/schedule-plan")
    public ResponseEntity<?> saveSchedulePlan(@PathVariable String userId, @RequestBody SchedulePlanRequestDto dto) {
        orderService.saveSchedulePlan(userId, dto);
        return ResponseEntity.ok("Schedule plan saved.");
    }

    @PostMapping("/{userId}/contact-info")
    public ResponseEntity<?> saveContactInfo(@PathVariable String userId, @RequestBody ContactDetailsDto dto) {
        orderService.saveContactInfo(userId, dto);
        return ResponseEntity.ok("Contact info saved.");
    }

    @PostMapping("/{userId}/finalize")
    public ResponseEntity<?> finalizeBooking(@PathVariable String userId) {
        try {
            redisTemplate.opsForHash().put(redisKey(userId), "confirmed", "true");
            OrderResponseDto createdOrderResponse = orderService.createOrder(userId);
            return ResponseEntity.ok(createdOrderResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to finalize booking: " + e.getMessage());
        }
    }

}
