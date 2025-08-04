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



    @PostMapping("/verify-pickup")
    public ResponseEntity<?> verifyPickupOtp(@RequestBody OtpRequest request, HttpServletRequest httpRequest) {
        log.info("Verify pickup OTP called for order: {}", request.getOrderId());
        log.info("OTP input: {}", request.getOtp());
        try {
            // Manually extract token from Authorization header to avoid recursion
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7); // Remove "Bearer "
            String userId = (String) jwtService.extractUserId(token);

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

            otpOrderEmailTransitionService.verifyPickupOtp(request.getOrderId(), request.getOtp(), roleId, role);
            return ResponseEntity.ok("Pickup OTP verified successfully.");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Bad request during pickup OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("⚠️ Invalid state during pickup OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("❗ Unexpected error verifying pickup OTP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Server error verifying pickup OTP.");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class OtpRequest {
        private String orderId;
        private String otp;
    }

    @PostMapping("/verify-handover")
    public ResponseEntity<?> verifyHandoverOtp(@RequestBody OtpRequest request, HttpServletRequest httpRequest) {
        log.info("Verify handover OTP called for order: {}", request.getOrderId());
        log.info("OTP input: {}", request.getOtp());

        try {
            // Extract Bearer token manually
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7);
            String userId = (String) jwtService.extractUserId(token);
            Users user = roleCheckingService.checkUser(userId);

            // Only Delivery Agent can verify handover OTP
            if (user.getRole() != UserRole.DELIVERY_AGENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only delivery agents can verify handover OTP.");
            }

            DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
            String agentId = agent.getDeliveryAgentId();

            otpOrderEmailTransitionService.verifyHandoverOtp(request.getOrderId(), request.getOtp(), agentId);
            return ResponseEntity.ok("Handover OTP verified successfully. Order marked as IN_CLEANING.");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Bad request during handover OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("⚠️ Invalid state during handover OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("❗ Unexpected error verifying handover OTP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Server error verifying handover OTP.");
        }
    }


    @PostMapping("/verify-delivery")
    public ResponseEntity<?> verifyDeliveryOtp(@RequestBody OtpRequest request, HttpServletRequest httpRequest) {
        log.info("Verify delivery OTP called for order: {}", request.getOrderId());
        log.info("OTP input: {}", request.getOtp());

        try {
            // Manually extract token from Authorization header to avoid recursion
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7); // Remove "Bearer "
            String userId = (String) jwtService.extractUserId(token);

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

            otpOrderEmailTransitionService.verifyDeliveryOtp(request.getOrderId(), request.getOtp(), roleId, role);
            return ResponseEntity.ok("Delivery OTP verified. Order marked as DELIVERED.");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Bad request during delivery OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("⚠️ Invalid state during delivery OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("❗ Unexpected error verifying delivery OTP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Server error verifying delivery OTP.");
        }
    }


    @PostMapping("/verify-confirm-for-cloths")
    public ResponseEntity<?> verifyConfirmForClothsOtp(@RequestBody OtpRequest request, HttpServletRequest httpRequest) {
        log.info("Verify confirm-for-cloths OTP called for order: {}", request.getOrderId());
        log.info("OTP input: {}", request.getOtp());

        try {
            // Extract Bearer token manually
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
            }

            String token = authHeader.substring(7);
            String userId = (String) jwtService.extractUserId(token);
            Users user = roleCheckingService.checkUser(userId);

            // Only Delivery Agent can verify confirm-for-cloths OTP
            if (user.getRole() != UserRole.DELIVERY_AGENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only delivery agents can verify this OTP.");
            }

            DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
            String agentId = agent.getDeliveryAgentId();

            otpOrderEmailTransitionService.verifyConfirmForClothsOtp(request.getOrderId(), request.getOtp(), agentId);
            return ResponseEntity.ok("OTP for cloth confirmation verified via email. Delivery OTP sent to customer.");

        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Bad request during confirm-for-cloths OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("⚠️ Invalid state during confirm-for-cloths OTP verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("❗ Unexpected error verifying confirm-for-cloths OTP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Server error verifying confirm-for-cloths OTP.");
        }
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