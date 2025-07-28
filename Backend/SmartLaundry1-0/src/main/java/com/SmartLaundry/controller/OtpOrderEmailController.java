package com.SmartLaundry.controller;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.JWTService;
import com.SmartLaundry.service.OrderEmailOtpService;
import com.SmartLaundry.service.OtpOrderEmailTransitionService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    @Autowired
    private OrderEmailOtpService orderEmailOtpService;
    private final OtpOrderEmailTransitionService otpOrderEmailTransitionService;
    private final OrderRepository orderRepository;



//    @PostMapping("/verify-pickup")
//    public ResponseEntity<?> verifyPickupOtp(@RequestBody OtpRequest request, HttpServletRequest token) {
//        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
//        Users user = roleCheckingService.checkUser(userId);
//        String agentId = null;
//
//        if (user.getRole() == UserRole.DELIVERY_AGENT) {
//            DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(userId)
//                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
//            agentId = agent.getDeliveryAgentId();
//        }
//
//        try {
//            otpOrderEmailTransitionService.verifyPickupOtp(request.getOrderId(), request.getOtp(), agentId);
//            return ResponseEntity.ok("OTP verified successfully.");
//        } catch (Exception e) {
//            log.error("❗ Error verifying pickup OTP: {}", e.getMessage(), e);
//            return ResponseEntity.internalServerError().body("Error verifying OTP.");
//        }
//    }
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    @Data
//    public static class OtpRequest {
//        private String orderId;
//        private String otp;
//    }
@PostMapping("/verify-pickup")
public ResponseEntity<?> verifyPickupOtp(@RequestBody OtpRequest request, HttpServletRequest token) {
    String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
    Users user = roleCheckingService.checkUser(userId);

    String roleId = null;
    UserRole role = user.getRole();

    if (role == UserRole.DELIVERY_AGENT) {
        DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
        roleId = agent.getDeliveryAgentId();
    } else if (role == UserRole.SERVICE_PROVIDER) {
        ServiceProvider provider = serviceProviderRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Service provider not found for user ID: " + userId));

        roleId = provider.getProviderId();
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only delivery agents or service providers can verify pickup OTP.");
    }

    try {
        otpOrderEmailTransitionService.verifyPickupOtp(request.getOrderId(), request.getOtp(), roleId, role);
        return ResponseEntity.ok("Pickup OTP verified successfully.");
    } catch (Exception e) {
        log.error("❗ Error verifying pickup OTP: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError().body("Error verifying pickup OTP.");
    }
}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class OtpRequest {
        private String orderId;
        private String otp;
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
        if (user.getRole().equals(UserRole.DELIVERY_AGENT)) {
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
//        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
//        Users user = roleCheckingService.checkUser(userId);
//
//        String id = null;
//        if (user.getRole().equals(UserRole.DELIVERY_AGENT)) {
//            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
//            id = deliveryAgent != null ? deliveryAgent.getDeliveryAgentId() : null;
//        } else if (user.getRole().equals(UserRole.SERVICE_PROVIDER)) {
//            id = user.getUserId(); // Service provider uses Users.userId
//        }
//
//        otpOrderEmailTransitionService.verifyDeliveryOtp(orderId, otp, id);
//        return ResponseEntity.ok("Delivery OTP verified via email. Order marked as DELIVERED.");
//    }

    @PostMapping("/verify-delivery")
    public ResponseEntity<?> verifyDeliveryOtp(@RequestBody OtpRequest request, HttpServletRequest token) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(token));
        Users user = roleCheckingService.checkUser(userId);

        String roleId = null;
        UserRole role = user.getRole();

        if (role == UserRole.DELIVERY_AGENT) {
            DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
            roleId = agent.getDeliveryAgentId();
        } else if (role == UserRole.SERVICE_PROVIDER) {
            ServiceProvider provider = serviceProviderRepository.findByUser_UserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("Service provider not found for user ID: " + userId));
            roleId = provider.getProviderId();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only delivery agents or service providers can verify delivery OTP.");
        }

        try {
            otpOrderEmailTransitionService.verifyDeliveryOtp(request.getOrderId(), request.getOtp(), roleId, role);
            return ResponseEntity.ok("Delivery OTP verified. Order marked as DELIVERED.");
        } catch (Exception e) {
            log.error("❗ Error verifying delivery OTP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error verifying delivery OTP.");
        }
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
        if (user.getRole().equals(UserRole.DELIVERY_AGENT)) {
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
