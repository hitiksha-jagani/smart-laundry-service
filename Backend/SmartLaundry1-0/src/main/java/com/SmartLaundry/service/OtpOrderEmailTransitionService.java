package com.SmartLaundry.service;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpOrderEmailTransitionService {

    private final OrderRepository orderRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final OrderEmailOtpService orderEmailOtpService;
    private final OrderStatusHistoryService orderStatusHistoryService;

    // 1. Verifies the OTP provided by the customer for pickup. If valid:
    //    - Sets status to PICKED_UP
    //    - Sends HANDOVER OTP to provider (if delivery agent is needed), else moves to IN_CLEANING
    public void verifyPickupOtp(String orderId, String otpInput, String agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Boolean needsAgent = order.getServiceProvider().getNeedOfDeliveryAgent();
        DeliveryAgent agent = null;
        if (Boolean.TRUE.equals(needsAgent)) {
            agent = deliveryAgentRepository.findById(agentId)
                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
        }

        if (!orderEmailOtpService.validateOtp(order, otpInput, OtpPurpose.PICKUP_CUSTOMER)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.PICKED_UP);

        if (Boolean.TRUE.equals(needsAgent)) {
            Users providerUser = order.getServiceProvider().getUser();
            orderEmailOtpService.generateAndSendOtp(
                    order,
                    providerUser, // ✅ fix: pass the correct user
                    agent,
                    OtpPurpose.HANDOVER_TO_PROVIDER,
                    providerUser.getEmail()
            );

        } else {
            order.setStatus(OrderStatus.IN_CLEANING);
            orderRepository.save(order);
            orderStatusHistoryService.save(order, OrderStatus.IN_CLEANING);
        }
    }

    // 2. Verifies HANDOVER OTP for provider (sent via delivery agent)
    public void verifyHandoverOtp(String orderId, String otpInput, String agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));

        if (!orderEmailOtpService.validateOtp(order, otpInput, OtpPurpose.HANDOVER_TO_PROVIDER)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.IN_CLEANING);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.IN_CLEANING);
    }

    // 3. Verifies the CONFIRM_FOR_CLOTHS OTP from provider to initiate delivery
    public void verifyConfirmForClothsOtp(String orderId, String otpInput, String agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));

        if (!orderEmailOtpService.validateOtp(order, otpInput, OtpPurpose.CONFIRM_FOR_CLOTHS)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.OUT_FOR_DELIVERY);

        orderEmailOtpService.generateAndSendOtp(
                order,
                order.getUsers(),
                agent,
                OtpPurpose.DELIVERY_CUSTOMER,
                order.getUsers().getEmail()
        );
    }

    // 4. Final OTP verification at delivery
    public void verifyDeliveryOtp(String orderId, String otpInput, String verifierId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Boolean needsAgent = order.getServiceProvider().getNeedOfDeliveryAgent();

        if (Boolean.TRUE.equals(needsAgent)) {
            if (order.getDeliveryDeliveryAgent() == null ||
                    !order.getDeliveryDeliveryAgent().getUsers().getUserId().equals(verifierId)) {
                throw new IllegalArgumentException("Only assigned delivery agent can verify this OTP.");
            }
        } else {
            if (!order.getServiceProvider().getUser().getUserId().equals(verifierId)) {
                throw new IllegalArgumentException("Only assigned service provider can verify this OTP.");
            }
        }

        if (!orderEmailOtpService.validateOtp(order, otpInput, OtpPurpose.DELIVERY_CUSTOMER)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.DELIVERED);
    }

    // 5. Resend OTP based on purpose
    public void resendOtp(String orderId, OtpPurpose purpose) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        String email = switch (purpose) {
            case PICKUP_CUSTOMER, DELIVERY_CUSTOMER -> order.getUser().getEmail();
            case HANDOVER_TO_PROVIDER, CONFIRM_FOR_CLOTHS -> order.getServiceProvider().getUser().getEmail();
            default -> throw new IllegalArgumentException("Unsupported purpose");
        };

        orderEmailOtpService.generateAndSendOtp(order, null, null, purpose, email);
    }
}
