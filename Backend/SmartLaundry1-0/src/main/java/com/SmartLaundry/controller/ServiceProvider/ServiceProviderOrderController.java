package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.Customer.TicketResponseDto;
import com.SmartLaundry.dto.OtpVerificationResponseDTO;
import com.SmartLaundry.dto.ServiceProvider.*;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/provider/orders")
@RequiredArgsConstructor
public class ServiceProviderOrderController {
    @Autowired
    private final ServiceProviderOrderService serviceProviderOrderService;
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final UserRepository usersRepository;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final ServiceProviderRepository serviceProviderRepository;
    @Autowired
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
        List<ActiveOrderGroupedDto> pendingOrders = serviceProviderOrderService.getPendingOrdersForServiceProvider(spUserId);
        return ResponseEntity.ok(pendingOrders);
    }

    @PostMapping("/accept/{orderId}")
    public ResponseEntity<OrderResponseDto> acceptOrder(HttpServletRequest request,
                                                        @PathVariable String orderId) {
        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        OrderResponseDto response = serviceProviderOrderService.acceptOrder(
                spUserId,
                orderId
        );
        return ResponseEntity.ok(response);
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
    public ResponseEntity<String> markInProgress(
            @PathVariable String orderId,
            HttpServletRequest request) throws AccessDeniedException {

        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.markOrderInCleaning(spUserId, orderId);

        return ResponseEntity.ok("Order marked as INPROGRESS");
    }
    @PutMapping("/{orderId}/ready-for-delivery")
    public ResponseEntity<String> markReadyForDelivery(
            @PathVariable String orderId,
            HttpServletRequest request) throws AccessDeniedException, JsonProcessingException {

        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.markOrderReadyForDelivery(spUserId, orderId);

        return ResponseEntity.ok("Order marked as READY_FOR_DELIVERY");
    }
//    @GetMapping("/pending-otp-verification")
//    public List<OtpPendingOrderDto> getOrdersPendingOtp() {
//        return orderService.getOrdersPendingOtp(); // filter by IN_CLEANING or OUT_FOR_DELIVERY
//    }

    @GetMapping("/pending-otp-verification")
    public ResponseEntity<List<OrderResponseDto>> getOrdersPendingOtpVerification(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeader(request);
        String userId = jwtService.extractUserId(token).toString();// use static method

        Object userIdObj = jwtService.extractUserId(token);
        if (userIdObj == null) {
            throw new RuntimeException("User ID missing in token.");
        }
        ServiceProvider provider = serviceProviderRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("No service provider found for userId: " + userId));

        List<Order> orders = orderRepository.findAllByServiceProviderAndOtpVerificationRequired(provider);

        List<OrderResponseDto> response = orders.stream()
                .map(orderMapper::toOtpVerificationDto)
                .toList();

        return ResponseEntity.ok(response);
    }


    // to fetch provider id from user id in token
   @GetMapping("/from-user/{userId}")
   public ResponseEntity<String> getProviderIdByUserId(@PathVariable String userId) {
       System.out.println("🔍 Looking for ServiceProvider with userId: " + userId);
       return serviceProviderRepository.findByUserUserId(userId)
               .map(sp -> {
                   System.out.println("✅ Found provider: " + sp.getServiceProviderId());
                   return ResponseEntity.ok(sp.getServiceProviderId());
               })
               .orElseGet(() -> {
                   System.out.println("❌ No provider found for userId: " + userId);
                   return ResponseEntity.notFound().build();
               });
   }


    @GetMapping("/active")
    public ResponseEntity<List<ActiveOrderGroupedDto>> getActiveOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        List<ActiveOrderGroupedDto> activeOrders = serviceProviderOrderService.getActiveOrdersForServiceProvider(spUserId);
        return ResponseEntity.ok(activeOrders);
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<ActiveOrderGroupedDto>> getDeliveredOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        List<ActiveOrderGroupedDto> deliveredOrders = serviceProviderOrderService.getDeliveredOrdersForServiceProvider(spUserId);
        return ResponseEntity.ok(deliveredOrders);
    }

//    @GetMapping("/serviceProviders/{providerId}/completed-orders")
//    public ResponseEntity<List<OrderHistoryDto>> getCompletedOrdersForProvider(
//            @PathVariable String providerId) {
//
//        List<OrderHistoryDto> orders = orderService.getCompletedOrdersForProvider(providerId);
//        return ResponseEntity.ok(orders);
//    }

    @PostMapping("/feedbackprovider/respond/{feedbackId}")
    public ResponseEntity<String> respondToFeedback(
            HttpServletRequest request,
            @PathVariable Long feedbackId,
            @RequestBody Map<String, String> requestBody) {

        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        String responseMessage = requestBody.get("response");

        serviceProviderOrderService.respondToFeedbackByUserId(spUserId, feedbackId, responseMessage);

        return ResponseEntity.ok("Response submitted successfully");
    }


    //to Fetch all feedbacks
    @GetMapping("{providerId}/feedbacks")
    public ResponseEntity<List<FeedbackResponseDto>> getFeedbacksForProvider(@PathVariable String providerId) {
        List<FeedbackResponseDto> feedbacks = serviceProviderOrderService.getFeedbackForServiceProvider(providerId);
        return ResponseEntity.ok(feedbacks);
    }

    //To fetch all Orders
    @GetMapping("/{providerId}/order-history")
    public ResponseEntity<List<OrderHistoryDto>> getOrderHistory(
            @PathVariable String providerId,
            @RequestParam(required = false) String status) {

        List<OrderHistoryDto> orders = serviceProviderOrderService.getOrderHistoryForProvider(providerId, status);
        return ResponseEntity.ok(orders);
    }

}

