package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.Customer.TicketResponseDto;
import com.SmartLaundry.dto.ServiceProvider.*;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

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
            HttpServletRequest request) throws AccessDeniedException {

        String spUserId = getServiceProviderUserId(request);
        checkIfBlocked(spUserId);
        serviceProviderOrderService.markOrderReadyForDelivery(spUserId, orderId);

        return ResponseEntity.ok("Order marked as READY_FOR_DELIVERY");
    }


    @GetMapping("/active")
    public ResponseEntity<List<ActiveOrderGroupedDto>> getActiveOrders(HttpServletRequest request) {
        String spUserId = getServiceProviderUserId(request);
        List<ActiveOrderGroupedDto> activeOrders = serviceProviderOrderService.getActiveOrdersForServiceProvider(spUserId);
        return ResponseEntity.ok(activeOrders);
    }


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

