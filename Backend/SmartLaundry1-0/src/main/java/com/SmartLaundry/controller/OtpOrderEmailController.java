package com.SmartLaundry.controller;
import com.SmartLaundry.model.OtpPurpose;
import com.SmartLaundry.service.OtpOrderEmailTransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emailotp")
@RequiredArgsConstructor
public class OtpOrderEmailController {

    private final OtpOrderEmailTransitionService otpOrderEmailTransitionService;

    @PostMapping("/verify-pickup")
    public ResponseEntity<?> verifyPickupOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam(required = false) String agentId
    ) {
        otpOrderEmailTransitionService.verifyPickupOtp(orderId, otp, agentId);
        return ResponseEntity.ok("Pickup OTP verified via email. Order status updated accordingly.");
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

