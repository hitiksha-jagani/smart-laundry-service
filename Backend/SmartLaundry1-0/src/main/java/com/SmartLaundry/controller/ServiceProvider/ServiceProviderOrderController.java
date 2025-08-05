package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.OtpVerificationResponseDTO;
import com.SmartLaundry.dto.ServiceProvider.*;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.JWTService;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/provider/orders")
@RequiredArgsConstructor
public class ServiceProviderOrderController {

    private final ServiceProviderOrderService serviceProviderOrderService;
    private final JWTService jwtService;
    private final OrderService orderService;
    private final UserRepository usersRepository;
    private final OrderRepository orderRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final OrderMapper orderMapper;

    private String getServiceProviderUserId(HttpServletRequest request) {
        return (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
    }

    private void checkIfBlocked(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isBlocked()) {
            throw new RuntimeException("Your account is blocked by admin. You cannot perform this action.");
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ActiveOrderGroupedDto>> getPendingOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        return ResponseEntity.ok(serviceProviderOrderService.getPendingOrdersForServiceProvider(spUserId));
    }

    @PostMapping("/accept/{orderId}")
    public ResponseEntity<OrderResponseDto> acceptOrder(HttpServletRequest request,
                                                        @PathVariable String orderId) {
        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        return ResponseEntity.ok(serviceProviderOrderService.acceptOrder(spUserId, orderId));
    }

    @PostMapping("/{orderId}/reject")
    public ResponseEntity<String> rejectOrder(HttpServletRequest request,
                                              @PathVariable String orderId) {
        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.rejectOrder(spUserId, orderId);
        return ResponseEntity.ok("Order rejected successfully.");
    }

    @PutMapping("/{orderId}/incleaning")
    public ResponseEntity<String> markInProgress(@PathVariable String orderId, HttpServletRequest request)
            throws AccessDeniedException {

        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.markOrderInCleaning(spUserId, orderId);
        return ResponseEntity.ok("Order marked as IN_CLEANING");
    }

    @PutMapping("/{orderId}/ready-for-delivery")
    public ResponseEntity<String> markReadyForDelivery(@PathVariable String orderId, HttpServletRequest request)
            throws AccessDeniedException, JsonProcessingException {

        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.markOrderReadyForDelivery(spUserId, orderId);
        return ResponseEntity.ok("Order marked as READY_FOR_DELIVERY");
    }

    @GetMapping("/pending-otp-verification")
    public ResponseEntity<List<OrderResponseDto>> getOrdersPendingOtpVerification(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeader(request);
        String userId = jwtService.extractUserId(token).toString();

        ServiceProvider provider = serviceProviderRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No service provider found for userId: " + userId));

        List<Order> orders = orderRepository.findAllByServiceProviderAndOtpVerificationRequired(provider);

        List<OrderResponseDto> response = orders.stream()
                .map(orderMapper::toOtpVerificationDto)
                .toList();

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/from-user/{userId}")
//    public ResponseEntity<String> getProviderIdByUserId(@PathVariable String userId) {
//        return serviceProviderRepository.findByUserUserId(userId)
//                .map(sp -> ResponseEntity.ok(sp.getServiceProviderId()))
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }


    @GetMapping("/from-user/{userId}")
    public ResponseEntity<?> getProviderIdByUserId(@PathVariable String userId) {
        return serviceProviderRepository.findByUserUserId(userId)
                .map(sp -> ResponseEntity.ok(sp.getServiceProviderId()))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Please complete your profile to access this feature."));
    }


    @GetMapping("/active")
    public ResponseEntity<List<ActiveOrderGroupedDto>> getActiveOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        return ResponseEntity.ok(serviceProviderOrderService.getActiveOrdersForServiceProvider(spUserId));
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<ActiveOrderGroupedDto>> getDeliveredOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        return ResponseEntity.ok(serviceProviderOrderService.getDeliveredOrdersForServiceProvider(spUserId));
    }

    @PostMapping("/feedbackprovider/respond/{feedbackId}")
    public ResponseEntity<String> respondToFeedback(HttpServletRequest request,
                                                    @PathVariable Long feedbackId,
                                                    @RequestBody Map<String, String> requestBody) {
        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.respondToFeedbackByUserId(spUserId, feedbackId, requestBody.get("response"));
        return ResponseEntity.ok("Response submitted successfully");
    }

    @GetMapping("{providerId}/feedbacks")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksForProvider(@PathVariable String providerId) {
        return ResponseEntity.ok(serviceProviderOrderService.getFeedbackForServiceProvider(providerId));
    }

    @GetMapping("/{providerId}/order-history")
    public ResponseEntity<List<OrderHistoryDto>> getOrderHistory(@PathVariable String providerId,
                                                                 @RequestParam(required = false) String status) {
        return ResponseEntity.ok(serviceProviderOrderService.getOrderHistoryForProvider(providerId, status));
    }
}
