package com.SmartLaundry.controller;
import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OtpPurpose;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.service.OtpOrderEmailTransitionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/emailotp")
@RequiredArgsConstructor
@Slf4j
public class OtpOrderEmailController {

    private final OtpOrderEmailTransitionService otpOrderEmailTransitionService;
    private final OrderRepository orderRepository;

//    @PostMapping("/verify-pickup")
//    public ResponseEntity<?> verifyPickupOtp(
//            @RequestParam String orderId,
//            @RequestParam String otp,
//            @RequestParam(required = false) String agentId
//    ) {
//        otpOrderEmailTransitionService.verifyPickupOtp(orderId, otp, agentId);
//        return ResponseEntity.ok("Pickup OTP verified via email. Order status updated accordingly.");
//    }

    @PostMapping("/verify-pickup")
    public ResponseEntity<?> verifyPickupOtp(@RequestBody OtpRequest request) {
        otpOrderEmailTransitionService.verifyPickupOtp(request.getOrderId(), request.getOtp(), request.getAgentId());
        return ResponseEntity.ok("Pickup OTP verified via email. Order status updated accordingly.");
    }

    @Data
    public static class OtpRequest {
        private String orderId;
        private String otp;
        private String agentId; // optional
    }

    @PostMapping("/verify-handover")
    public ResponseEntity<?> verifyHandoverOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam String agentId
    ) {
        otpOrderEmailTransitionService.verifyHandoverOtp(orderId, otp, agentId);
        return ResponseEntity.ok("Handover OTP verified via email. Order marked as IN_CLEANING.");
    }

    @PostMapping("/verify-delivery")
    public ResponseEntity<?> verifyDeliveryOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam String verifierId
    ) {
        otpOrderEmailTransitionService.verifyDeliveryOtp(orderId, otp, verifierId);
        return ResponseEntity.ok("Delivery OTP verified via email. Order marked as DELIVERED.");
    }

    @PostMapping("/verify-confirm-for-cloths")
    public ResponseEntity<?> verifyConfirmForClothsOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam String agentId
    ) {
        otpOrderEmailTransitionService.verifyConfirmForClothsOtp(orderId, otp, agentId);
        return ResponseEntity.ok("OTP for cloth confirmation verified via email. Delivery OTP sent to customer.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(
            @RequestParam String orderId,
            @RequestParam OtpPurpose purpose
    ) {
        otpOrderEmailTransitionService.resendOtp(orderId, purpose);
        return ResponseEntity.ok("OTP resent via email.");
    }


}

