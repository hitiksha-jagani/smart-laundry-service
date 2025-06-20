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
                .expiresAt(now.plusMinutes(10))
                .isUsed(false)
                .purpose(purpose)
                .build();

        orderOtpRepository.save(orderOtp);
        emailService.sendOtp(recipientEmail, otp);
    }

    public boolean validateOtp(Order order, String inputOtp, OtpPurpose purpose) {
        Optional<OrderOtp> validOtp = orderOtpRepository.findTopByOrderAndPurposeAndIsUsedFalseOrderByGeneratedAtDesc(order, purpose);

        if (validOtp.isEmpty()) return false;

        OrderOtp otp = validOtp.get();

        if (otp.getExpiresAt().isBefore(LocalDateTime.now()) || !otp.getOtpCode().equals(inputOtp)) return false;

        otp.setIsUsed(true);
        orderOtpRepository.save(otp);
        return true;
    }

    private String generateOtpCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}

