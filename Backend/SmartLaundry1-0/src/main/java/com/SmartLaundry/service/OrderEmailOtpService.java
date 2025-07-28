package com.SmartLaundry.service;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.OrderOtpRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.service.Customer.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderEmailOtpService {

    private final OrderOtpRepository orderOtpRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final OrderStatusHistoryService orderStatusHistoryService;

    public void generateAndSendOtp(Order order, Users user, DeliveryAgent agent, OtpPurpose purpose, String recipientEmail) {
        String otp = generateOtpCode();
        LocalDateTime now = LocalDateTime.now();

        OrderOtp orderOtp = OrderOtp.builder()
                .order(order)
                .user(user)
                .agent(agent)
                .otpCode(otp)
                .generatedAt(now)
                .expiresAt(now.plusDays(30))
                .isUsed(false)
                .purpose(purpose)
                .build();

        orderOtpRepository.save(orderOtp);
        emailService.sendOtp(recipientEmail, otp);
    }

//    public boolean validateOtp(Order order, String inputOtp, OtpPurpose purpose) {
//        Optional<OrderOtp> validOtp = orderOtpRepository.findTopByOrderAndPurposeAndIsUsedFalseOrderByGeneratedAtDesc(order, purpose);
//
//        if (validOtp.isEmpty()) return false;
//
//        OrderOtp otp = validOtp.get();
//
//        if (otp.getExpiresAt().isBefore(LocalDateTime.now()) || !otp.getOtpCode().equals(inputOtp)) return false;
//
//        otp.setIsUsed(true);
//        orderOtpRepository.save(otp);
//        return true;
//    }
public boolean validateOtp(Order order, String inputOtp, OtpPurpose purpose) {
    Optional<OrderOtp> validOtp = orderOtpRepository.findTopByOrderAndPurposeAndIsUsedFalseOrderByGeneratedAtDesc(order, purpose);

    if (validOtp.isEmpty()) {
        System.out.println("❌ No valid OTP found: maybe already used or none generated yet.");
        return false;
    }

    OrderOtp otp = validOtp.get();

    System.out.printf("🔍 OTP match attempt for order=%s, purpose=%s, input=%s, dbOtp=%s, expiresAt=%s%n",
            order.getOrderId(), purpose, inputOtp, otp.getOtpCode(), otp.getExpiresAt());

    if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
        System.out.println("❌ OTP expired.");
        return false;
    }

    if (!otp.getOtpCode().equals(inputOtp)) {
        System.out.println("❌ OTP does not match.");
        return false;
    }

    otp.setIsUsed(true);
    orderOtpRepository.save(otp);
    System.out.println("✅ OTP verified and marked as used.");
    return true;
}


    private String generateOtpCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}

