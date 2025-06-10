//package com.SmartLaundry.controller.Customer;
//
//import com.SmartLaundry.dto.Customer.*;
//import com.SmartLaundry.dto.DeliveryAgent.FeedbackAgentRequestDto;
//import com.SmartLaundry.service.Customer.OrderService;
//import com.SmartLaundry.service.JWTService;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/orders")
//@RequiredArgsConstructor
//public class OrderController {
//
//    private final OrderService orderService;
//    private final JWTService jwtService;
//
//    private String extractUserIdFromRequest(HttpServletRequest request) {
//        String token = extractHeader.extractTokenFromHeader(request);
//        if (token == null || token.isEmpty()) {
//            throw new IllegalArgumentException("Authorization token is missing");
//        }
//
//
//        // jwtService.extractUserId returns Object, cast safely to String
//        Object userIdObj = jwtService.extractUserId(token);
//        if (!(userIdObj instanceof String userId) || userId.isEmpty()) {
//            throw new IllegalArgumentException("Invalid token or userId");
//        }
//        return userId;
//    }
//
//    @PostMapping("/initial")
//    public ResponseEntity<String> saveInitialOrder(HttpServletRequest request, @RequestBody BookOrderRequestDto dto) {
//        String userId = extractUserIdFromRequest(request);
//        orderService.saveInitialOrderDetails(userId, dto);
//        return ResponseEntity.ok("Initial order details saved");
//    }
//
//    @PostMapping("/schedule-plan")
//    public ResponseEntity<String> saveSchedulePlan(HttpServletRequest request, @RequestBody SchedulePlanRequestDto dto) {
//        String userId = extractUserIdFromRequest(request);
//        orderService.saveSchedulePlan(userId, dto);
//        return ResponseEntity.ok("Schedule plan saved");
//    }
//
//    @PostMapping("/contact")
//    public ResponseEntity<String> saveContactInfo(HttpServletRequest request, @RequestBody ContactDetailsDto dto) {
//        String userId = extractUserIdFromRequest(request);
//        orderService.saveContactInfo(userId, dto);
//        return ResponseEntity.ok("Contact information saved");
//    }
//
//    @PostMapping("/place")
//    public ResponseEntity<String> placeOrder(HttpServletRequest request) {
//        try {
//            String userId = extractUserIdFromRequest(request);
//            orderService.placeOrder(userId);
//            return ResponseEntity.ok("Order placed and pending for provider review");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/finalize")
//    public ResponseEntity<OrderResponseDto> finalizeBooking(HttpServletRequest request) {
//        String userId = extractUserIdFromRequest(request);
//        OrderResponseDto createdOrder = orderService.createOrder(userId);
//        return ResponseEntity.ok(createdOrder);
//    }
//
//    @PostMapping("/cancel/{orderId}")
//    public ResponseEntity<String> cancelOrder(HttpServletRequest request, @PathVariable String orderId) {
//        String userId = extractUserIdFromRequest(request);
//        orderService.cancelOrder(userId, orderId);
//        return ResponseEntity.ok("Order canceled successfully");
//    }
//
//
//    @PostMapping("/reschedule/{orderId}")
//    public ResponseEntity<String> rescheduleOrder(
//            HttpServletRequest request,
//            @PathVariable String orderId,
//            @RequestBody RescheduleRequestDto dto) {
//
//        String userId = extractUserIdFromRequest(request);
//        orderService.rescheduleOrder(userId, orderId, dto);
//        return ResponseEntity.ok("Order rescheduled successfully");
//    }
//
//    @PostMapping("/provider-feedback")
//    public ResponseEntity<String> submitFeedback(HttpServletRequest request, @RequestBody FeedbackRequestDto dto) {
//        String userId = extractUserIdFromRequest(request);
//        orderService.submitFeedbackProviders(userId, dto);
//        return ResponseEntity.ok("Feedback submitted successfully");
//    }
//
//    @PostMapping("/agent-feedback")
//    public ResponseEntity<String> submitFeedbackToAgent(HttpServletRequest request, @RequestBody FeedbackAgentRequestDto dto) {
//        String userId = extractUserIdFromRequest(request);
//        orderService.submitFeedbackAgents(userId, dto);
//        return ResponseEntity.ok("Feedback submitted successfully");
//    }
//
//    @PostMapping("/raise-ticket")
//    public ResponseEntity<String> raiseTicket(
//            HttpServletRequest request,
//            @RequestBody RaiseTicketRequestDto dto) {
//
//        String userId = extractUserIdFromRequest(request);
//        orderService.raiseTicket(userId, dto);
//        return ResponseEntity.ok("Ticket raised successfully");
//    }
//
//
//
//    @GetMapping("/track/{orderId}")
//    public ResponseEntity<TrackOrderResponseDto> trackOrder(HttpServletRequest request, @PathVariable String orderId) {
//        String userId = extractUserIdFromRequest(request);
//        TrackOrderResponseDto dto = orderService.trackOrder(userId, orderId);
//        return ResponseEntity.ok(dto);
//    }
//
//
//}
