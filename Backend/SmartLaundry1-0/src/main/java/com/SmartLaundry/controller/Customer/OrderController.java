package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.Customer.*;
import com.SmartLaundry.dto.DeliveryAgent.FeedbackAgentRequestDto;
import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Promotion;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.PaymentRepository;
import com.SmartLaundry.repository.PromotionRepository;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.Customer.OrderSummaryService;
import com.SmartLaundry.service.Customer.PromotionService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.SmartLaundry.service.Customer.BillService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JWTService jwtService;
    private final OrderSummaryService orderSummaryService;
    private final PromotionRepository promotionRepository;
    private final BillService billService;
    private final PromotionService promotionService;
    private final OrderRepository orderRepository;
    private final BillRepository billRepository;
    private String extractUserIdFromRequest(HttpServletRequest request) {
        return (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
    }

    @PostMapping("/initial")
    public ResponseEntity<String> saveInitialOrder(HttpServletRequest request, @RequestBody BookOrderRequestDto dto) {
        String userId = extractUserIdFromRequest(request);
        String dummyOrderId = orderService.saveInitialOrderDetails(userId, dto);
        return ResponseEntity.ok(dummyOrderId); // Return dummyOrderId instead of a message
    }

    @PostMapping("/schedule-plan/{dummyOrderId}")
    public ResponseEntity<String> saveSchedulePlan(HttpServletRequest request,
                                                   @PathVariable String dummyOrderId,
                                                   @RequestBody SchedulePlanRequestDto dto) {
        String userId = extractUserIdFromRequest(request);
        orderService.saveSchedulePlan(userId, dummyOrderId, dto);
        return ResponseEntity.ok("Schedule plan saved");
    }

    @PostMapping("/contact/{dummyOrderId}")
    public ResponseEntity<String> saveContactInfo(HttpServletRequest request,
                                                  @PathVariable String dummyOrderId,
                                                  @RequestBody ContactDetailsDto dto) {
        String userId = extractUserIdFromRequest(request);
        orderService.saveContactInfo(userId, dummyOrderId, dto);
        return ResponseEntity.ok("Contact info saved");
    }



    @PostMapping("/place/{dummyOrderId}")
    public ResponseEntity<OrderResponseDto> finalizeOrder(HttpServletRequest request,
                                                          @PathVariable String dummyOrderId) {
        String userId = extractUserIdFromRequest(request);
        OrderResponseDto response = orderService.createOrder(userId, dummyOrderId);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(HttpServletRequest request, @PathVariable String orderId) {
        String userId = extractUserIdFromRequest(request);
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok("Order canceled successfully");
    }


    @PostMapping("/reschedule/{orderId}")
    public ResponseEntity<String> rescheduleOrder(
            HttpServletRequest request,
            @PathVariable String orderId,
            @RequestBody RescheduleRequestDto dto) {

        String userId = extractUserIdFromRequest(request);
        orderService.rescheduleOrder(userId, orderId, dto);
        return ResponseEntity.ok("Order rescheduled successfully");
    }

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

    @PostMapping("/provider-feedback/{orderId}")
    public ResponseEntity<String> submitFeedback(
            HttpServletRequest request,
            @PathVariable String orderId,
            @RequestBody FeedbackRequestDto dto) {

        String userId = extractUserIdFromRequest(request);
        dto.setOrderId(orderId);
        orderService.submitFeedbackProviders(userId, dto);
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    @PostMapping("/agent-feedback/{orderId}")
    public ResponseEntity<String> submitFeedbackToAgent(
            HttpServletRequest request,
            @PathVariable String orderId,
            @RequestBody FeedbackAgentRequestDto dto) {

        String userId = extractUserIdFromRequest(request);
        dto.setOrderId(orderId);
        orderService.submitFeedbackAgents(userId, dto);
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    @GetMapping("/track/{orderId}")
    public ResponseEntity<TrackOrderResponseDto> trackOrder(HttpServletRequest request, @PathVariable String orderId) {
        String userId = extractUserIdFromRequest(request);
        TrackOrderResponseDto dto = orderService.trackOrder(userId, orderId);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummaryDto> getOrderSummary(
            @PathVariable String orderId,
            @RequestParam(required = false) String promoCode) {

        Promotion promo = null;

        if (promoCode != null && !promoCode.isBlank()) {
            // Fetch promotion by promoCode (assume you have a PromotionRepository)
            promo = promotionRepository.findActiveByPromoCode(promoCode)
                    .orElse(null);
        }

        OrderSummaryDto summary = orderSummaryService.generateOrderSummary(orderId, promo);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/available-promotions")
    public ResponseEntity<List<Promotion>> getValidPromotionsForOrder(@RequestParam String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<Promotion> validPromos = promotionService.getAvailablePromotionsForOrder(order.getCreatedAt());
        return ResponseEntity.ok(validPromos);
    }
    @GetMapping("/promotion/{id}")
    public ResponseEntity<Promotion> testPromo(@PathVariable String id) {
        Promotion promo = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promo);
    }

    @PostMapping("/apply-promo")
    public ResponseEntity<?> applyPromoToOrder(
            @RequestParam String orderId,
            @RequestParam String promotionId
    ) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (promotionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Promotion not found with ID: " + promotionId);
        }

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Order not found with ID: " + orderId);
        }

        Order order = orderOpt.get();
        Promotion promotion = promotionOpt.get();

        order.setPromotion(promotion);
        orderRepository.save(order);

        OrderSummaryDto summary = orderSummaryService.generateOrderSummary(orderId, promotion);

        return ResponseEntity.ok(summary);
    }








}
