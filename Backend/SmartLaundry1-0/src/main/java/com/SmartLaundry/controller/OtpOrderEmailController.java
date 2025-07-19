package com.SmartLaundry.controller;
import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.JWTService;
import com.SmartLaundry.service.OtpOrderEmailTransitionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/emailotp")
@RequiredArgsConstructor
@Slf4j
public class OtpOrderEmailController {

    @Autowired
    private RoleCheckingService roleCheckingService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

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

//    @PostMapping("/verify-pickup")
//    public ResponseEntity<?> verifyPickupOtp(@RequestBody OtpRequest request) {
//        otpOrderEmailTransitionService.verifyPickupOtp(request.getOrderId(), request.getOtp(), request.getAgentId());
//        return ResponseEntity.ok("Pickup OTP verified via email. Order status updated accordingly.");
//    }

    @PostMapping("/verify-pickup")
    public ResponseEntity<?> verifyPickupOtp(@RequestBody OtpRequest request, HttpServletRequest token) {

        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
        Users user = roleCheckingService.checkUser(userId);

        String id = null;

        if(user.getRole().equals(UserRole.DELIVERY_AGENT)) {
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
            id = deliveryAgent.getDeliveryAgentId();
        }

        otpOrderEmailTransitionService.verifyPickupOtp(request.getOrderId(), request.getOtp(), id);
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
            HttpServletRequest token
    ) {

        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
        Users user = roleCheckingService.checkUser(userId);

        String id = null;

        if(user.getRole().equals(UserRole.DELIVERY_AGENT)) {
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
            id = deliveryAgent.getDeliveryAgentId();
        }

        otpOrderEmailTransitionService.verifyHandoverOtp(orderId, otp, id);
        return ResponseEntity.ok("Handover OTP verified via email. Order marked as IN_CLEANING.");
    }

//    @PostMapping("/verify-delivery")
//    public ResponseEntity<?> verifyDeliveryOtp(
//            @RequestParam String orderId,
//            @RequestParam String otp,
//            HttpServletRequest token
//    ) {
//
//        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
//        Users user = roleCheckingService.checkUser(userId);
//
//        String id = null;
//
//        if(user.getRole().equals(UserRole.DELIVERY_AGENT)) {
//            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
//            id = deliveryAgent.getDeliveryAgentId();
//        }
//
//        otpOrderEmailTransitionService.verifyDeliveryOtp(orderId, otp, id);
//        return ResponseEntity.ok("Delivery OTP verified via email. Order marked as DELIVERED.");
//    }

    @PostMapping("/verify-delivery")
    public ResponseEntity<?> verifyDeliveryOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            HttpServletRequest token
    ) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
        Users user = roleCheckingService.checkUser(userId);

        String id = null;

        if (user.getRole().equals(UserRole.DELIVERY_AGENT)) {
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
            id = deliveryAgent != null ? deliveryAgent.getDeliveryAgentId() : null;
        } else if (user.getRole().equals(UserRole.SERVICE_PROVIDER)) {
            id = user.getUserId(); // assuming ServiceProvider uses Users.userId
        }

        otpOrderEmailTransitionService.verifyDeliveryOtp(orderId, otp, id);
        return ResponseEntity.ok("Delivery OTP verified via email. Order marked as DELIVERED.");
    }


    @PostMapping("/verify-confirm-for-cloths")
    public ResponseEntity<?> verifyConfirmForClothsOtp(
            @RequestParam String orderId,
            @RequestParam String otp,
            HttpServletRequest token
    ) {

        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
        Users user = roleCheckingService.checkUser(userId);

        String id = null;

        if(user.getRole().equals(UserRole.DELIVERY_AGENT)) {
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
            id = deliveryAgent.getDeliveryAgentId();
        }

        otpOrderEmailTransitionService.verifyConfirmForClothsOtp(orderId, otp, id);
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

