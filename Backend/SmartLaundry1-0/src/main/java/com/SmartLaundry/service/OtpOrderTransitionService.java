package com.SmartLaundry.service;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpOrderTransitionService {

    private final OrderRepository orderRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final OrderOtpService orderOtpService;
    private final OrderStatusHistoryService orderStatusHistoryService;

    public void verifyPickupOtp(String orderId, String otpInput, String agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Boolean needsAgent = order.getServiceProvider().getNeedOfDeliveryAgent();

        DeliveryAgent agent = null;
        if (Boolean.TRUE.equals(needsAgent)) {
            agent = deliveryAgentRepository.findById(agentId)
                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
        }

        if (!orderOtpService.validateOtp(order, otpInput, OtpPurpose.PICKUP_CUSTOMER)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.PICKED_UP);

        if (Boolean.TRUE.equals(needsAgent)) {
            Users providerUser = order.getServiceProvider().getUser();
            orderOtpService.generateAndSendOtp(
                    order,
                    null,
                    agent,
                    OtpPurpose.HANDOVER_TO_PROVIDER,
                    providerUser.getPhoneNo()
            );
        } else {
            order.setStatus(OrderStatus.IN_CLEANING);
            orderRepository.save(order);
            orderStatusHistoryService.save(order, OrderStatus.IN_CLEANING);
        }
    }

    public void verifyHandoverOtp(String orderId, String otpInput, String agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));

        if (!orderOtpService.validateOtp(order, otpInput, OtpPurpose.HANDOVER_TO_PROVIDER)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.IN_CLEANING);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.IN_CLEANING);
    }

    public void verifyConfirmForClothsOtp(String orderId, String otpInput, String agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));

        if (!orderOtpService.validateOtp(order, otpInput, OtpPurpose.CONFIRM_FOR_CLOTHS)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // OTP valid – now send delivery OTP to customer
        orderOtpService.generateAndSendOtp(
                order,
                order.getUsers(),
                agent,
                OtpPurpose.DELIVERY_CUSTOMER,
                order.getUsers().getPhoneNo()
        );
    }

    public void verifyDeliveryOtp(String orderId, String otpInput, String verifierId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Boolean needsAgent = order.getServiceProvider().getNeedOfDeliveryAgent();

        if (Boolean.TRUE.equals(needsAgent)) {
            DeliveryAgent agent = deliveryAgentRepository.findById(verifierId)
                    .orElseThrow(() -> new IllegalArgumentException("Delivery agent not found"));
        } else {
            String providerId = order.getServiceProvider().getUser().getUserId();
            if (!providerId.equals(verifierId)) {
                throw new IllegalArgumentException("Unauthorized: only the service provider can confirm delivery for this order.");
            }
        }

        if (!orderOtpService.validateOtp(order, otpInput, OtpPurpose.DELIVERY_CUSTOMER)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        orderStatusHistoryService.save(order, OrderStatus.DELIVERED);
    }

    public void resendOtp(String orderId, OtpPurpose purpose) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        String phone = switch (purpose) {
            case PICKUP_CUSTOMER, DELIVERY_CUSTOMER ->
                    order.getUser().getPhoneNo();
            case HANDOVER_TO_PROVIDER, CONFIRM_FOR_CLOTHS ->
                    order.getServiceProvider().getUser().getPhoneNo();
            default -> throw new IllegalArgumentException("Unsupported purpose");
        };

        orderOtpService.generateAndSendOtp(order, null, null, purpose, phone);
    }
}
