//package com.SmartLaundry.controller.ServiceProvider;
//
//import com.SmartLaundry.dto.Customer.OrderResponseDto;
//import com.SmartLaundry.dto.Customer.TicketResponseDto;
//import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
//import com.SmartLaundry.service.Customer.OrderService;
//import com.SmartLaundry.service.JWTService;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/provider/orders")
//@RequiredArgsConstructor
//public class ServiceProviderOrderController {
//
//    private final ServiceProviderOrderService serviceProviderOrderService;
//    private final JWTService jwtService;
//    private final OrderService orderService;
//
////    private String getServiceProviderUserId(HttpServletRequest request) {
////        String token = extractHeader.extractTokenFromHeader(request);
////        if (token == null || token.isBlank()) {
////            throw new RuntimeException("Authorization token missing or empty");
////        }
////        Object userIdClaim = jwtService.extractClaim(token, claims -> claims.get("id"));
////        if (userIdClaim == null) {
////            throw new RuntimeException("User ID not found in token");
////        }
////        return userIdClaim.toString();
////    }
//
//    @GetMapping("/pending")
//    public ResponseEntity<List<OrderResponseDto>> getPendingOrders(HttpServletRequest request) {
//        String spUserId = getServiceProviderUserId(request);
//        List<OrderResponseDto> pendingOrders = serviceProviderOrderService.getPendingOrdersForServiceProvider(spUserId);
//        return ResponseEntity.ok(pendingOrders);
//    }
//
//    @PostMapping("/{orderId}/accept")
//    public ResponseEntity<OrderResponseDto> acceptOrder(HttpServletRequest request,
//                                                        @PathVariable String orderId) {
////        String spUserId = getServiceProviderUserId(request);
//        OrderResponseDto response = serviceProviderOrderService.acceptOrder(spUserId, orderId);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/{orderId}/reject")
//    public ResponseEntity<String> rejectOrder(HttpServletRequest request,
//                                              @PathVariable String orderId) {
////        String spUserId = getServiceProviderUserId(request);
//        serviceProviderOrderService.rejectOrder(spUserId, orderId);
//        return ResponseEntity.ok("Order rejected successfully.");
//    }
//
//    @PostMapping("/feedbackprovider/respond/{feedbackId}")
//    public ResponseEntity<String> respondToFeedback(
//            HttpServletRequest request,
//            @PathVariable Long feedbackId,
//            @RequestBody Map<String, String> requestBody) {
//
//        String spUserId = getServiceProviderUserId(request); // your method to get logged-in userId
//        String responseMessage = requestBody.get("response");
//
//        serviceProviderOrderService.respondToFeedbackByUserId(spUserId, feedbackId, responseMessage);
//
//        return ResponseEntity.ok("Response submitted successfully");
//    }
//
//    // feedback Response through Agent
//    @PostMapping("/feedbackagent/respond/{feedbackId}")
//    public ResponseEntity<String> respondToAgentFeedback(
//            HttpServletRequest request,
//            @PathVariable Long feedbackId,
//            @RequestBody Map<String, String> requestBody) {
//
//        // Extract userId of the delivery agent from the JWT or session
//        String agentUserId = getServiceProviderUserId(request); // implement this method according to your auth setup
//
//        String responseMessage = requestBody.get("response");
//        if (responseMessage == null || responseMessage.trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("Response message cannot be empty");
//        }
//
//        serviceProviderOrderService.respondToAgentFeedbackByUserId(agentUserId, feedbackId, responseMessage);
//
//        return ResponseEntity.ok("Response submitted successfully");
//    }
//
//
//    @PostMapping("/ticket/respond/{ticketId}")
//    public ResponseEntity<String> respondToTicket(
//            @PathVariable Long ticketId,
//            @RequestBody TicketResponseDto dto
//    ) {
//        try {
//            serviceProviderOrderService.respondToTicket(ticketId, dto);
//            return ResponseEntity.ok("Ticket responded successfully" +
//                    (dto.isMakeFaqVisible() ? " and added to FAQ." : "."));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(404).body("Error: " + e.getMessage());
//        }
//    }
//}
//
