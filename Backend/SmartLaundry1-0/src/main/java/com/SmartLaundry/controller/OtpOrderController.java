package com.SmartLaundry.controller;
import com.SmartLaundry.model.OtpPurpose;
import com.SmartLaundry.service.OtpOrderTransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderotp")
@RequiredArgsConstructor
public class OtpOrderController {

    private final OtpOrderTransitionService otpOrderTransitionService;
    @PostMapping("/verify-pickup")
    public ResponseEntity<?> verifyPickupOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam(required = false) String agentId
    ) {
        otpOrderTransitionService.verifyPickupOtp(orderId, otp, agentId);
        return ResponseEntity.ok("Pickup OTP verified. Order status updated accordingly.");
    }


    @PostMapping("/verify-handover")
    public ResponseEntity<?> verifyHandoverOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam String agentId
    ) {
        otpOrderTransitionService.verifyHandoverOtp(orderId, otp, agentId);
        return ResponseEntity.ok("Handover OTP verified. Order marked as IN_CLEANING.");
    }

    @PostMapping("/verify-delivery")
    public ResponseEntity<?> verifyDeliveryOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            @RequestParam String verifierId
    ) {
        otpOrderTransitionService.verifyDeliveryOtp(orderId, otp, verifierId);
        return ResponseEntity.ok("Delivery OTP verified. Order marked as DELIVERED.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(
            @RequestParam String orderId,
            @RequestParam OtpPurpose purpose
    ) {
        otpOrderTransitionService.resendOtp(orderId, purpose);
        return ResponseEntity.ok("OTP resent.");
    }


}
