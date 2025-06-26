
package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.Customer.TicketResponseDto;
import com.SmartLaundry.dto.ServiceProvider.*;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.Customer.SMSService;
import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import com.SmartLaundry.service.OrderEmailOtpService;
import com.SmartLaundry.service.OrderOtpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderOrderService {
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final ServiceProviderRepository serviceProviderRepository;
    @Autowired
    private final SMSService smsService;
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final OrderMapper orderMapper;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final FeedbackProvidersRepository feedbackProvidersRepository;
    @Autowired
    private final TicketRepository ticketRepository;
    @Autowired
    private final FAQRepository faqRepository;
    @Autowired
    private final DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    private final FeedbackAgentsRepository feedbackAgentsRepository;
    @Autowired
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    @Autowired
    private final DeliveriesService deliveryService;
    @Autowired
    private final OrderOtpService orderOtpService;
    private final OrderEmailOtpService orderEmailOtpService;
    private static final Logger log = LoggerFactory.getLogger(ServiceProviderOrderService.class);
    private static final long LOCK_EXPIRY_MILLIS = 10_000;
    private static final String LOCK_PREFIX = "lock:order:user:";

    private String getRedisKey(String orderId) {
        return "order:id:" + orderId;
    }

    public static String getPendingOrdersSetKey(String serviceProviderId) {
        return "sp:pendingOrders:" + serviceProviderId;
    }

    private boolean tryAcquireLock(String lockKey) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofMillis(LOCK_EXPIRY_MILLIS));
        return Boolean.TRUE.equals(success);
    }

    private void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }


    public List<ActiveOrderGroupedDto> getActiveOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new IllegalStateException("Service Provider not found"));

        List<Order> activeOrders = orderRepository.findByServiceProviderAndStatus(sp, OrderStatus.IN_CLEANING);

        return activeOrders.stream().map(order -> {
            List<ActiveOrderDto> itemDtos = order.getBookingItems().stream().map(item -> {
                Items itemEntity = item.getItem();

                String serviceName = Optional.ofNullable(itemEntity.getSubService())
                        .map(sub -> sub.getServices())
                        .map(Services::getServiceName)
                        .orElse(Optional.ofNullable(itemEntity.getService())
                                .map(Services::getServiceName)
                                .orElse("N/A"));

                String subServiceName = Optional.ofNullable(itemEntity.getSubService())
                        .map(SubService::getSubServiceName)
                        .orElse("N/A");

                return ActiveOrderDto.builder()
                        .itemName(itemEntity.getItemName())
                        .service(serviceName)
                        .subService(subServiceName)
                        .quantity(item.getQuantity())
                        .build();
            }).toList();

            return ActiveOrderGroupedDto.builder()
                    .orderId(order.getOrderId())
                    .pickupDate(order.getPickupDate())
                    .pickupTime(order.getPickupTime())
                    .status(order.getStatus())
                    .items(itemDtos)
                    .build();
        }).toList();
    }


    public List<ActiveOrderGroupedDto> getPendingOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new IllegalStateException("Service Provider not found"));

        List<Order> pendingOrders = orderRepository.findByServiceProviderAndStatus(sp, OrderStatus.PENDING);

        return pendingOrders.stream().map(order -> {
            List<ActiveOrderDto> itemDtos = order.getBookingItems().stream().map(item -> {
                Items itemEntity = item.getItem();

                String serviceName = Optional.ofNullable(itemEntity.getSubService())
                        .map(sub -> sub.getServices())
                        .map(Services::getServiceName)
                        .orElse(Optional.ofNullable(itemEntity.getService())
                                .map(Services::getServiceName)
                                .orElse("N/A"));

                String subServiceName = Optional.ofNullable(itemEntity.getSubService())
                        .map(SubService::getSubServiceName)
                        .orElse("N/A");

                return ActiveOrderDto.builder()
                        .itemName(itemEntity.getItemName())
                        .service(serviceName)
                        .subService(subServiceName)
                        .quantity(item.getQuantity())
                        .build();
            }).toList();

            return ActiveOrderGroupedDto.builder()
                    .orderId(order.getOrderId())
                    .pickupDate(order.getPickupDate())
                    .pickupTime(order.getPickupTime())
                    .status(order.getStatus())
                    .items(itemDtos)
                    .build();
        }).toList();
    }

    public List<OrderResponseDto> getOtpPendingOrders(String providerId) {
        List<Order> orders = orderRepository.findOrdersWithPendingOtpForProvider(providerId);
        return orders.stream()
                .map(orderMapper::toOrderResponseDto)
                .collect(Collectors.toList());
    }

    public List<ActiveOrderGroupedDto> getDeliveredOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new IllegalStateException("Service Provider not found"));

        List<Order> deliveredOrders = orderRepository.findByServiceProviderAndStatus(sp, OrderStatus.DELIVERED);

        return deliveredOrders.stream().map(order -> {
            List<ActiveOrderDto> itemDtos = order.getBookingItems().stream().map(item -> {
                Items itemEntity = item.getItem();

                String serviceName = Optional.ofNullable(itemEntity.getSubService())
                        .map(sub -> sub.getServices())
                        .map(Services::getServiceName)
                        .orElse(Optional.ofNullable(itemEntity.getService())
                                .map(Services::getServiceName)
                                .orElse("N/A"));

                String subServiceName = Optional.ofNullable(itemEntity.getSubService())
                        .map(SubService::getSubServiceName)
                        .orElse("N/A");

                return ActiveOrderDto.builder()
                        .itemName(itemEntity.getItemName())
                        .service(serviceName)
                        .subService(subServiceName)
                        .quantity(item.getQuantity())
                        .build();
            }).toList();

            return ActiveOrderGroupedDto.builder()
                    .orderId(order.getOrderId())
                    .pickupDate(order.getPickupDate())
                    .pickupTime(order.getPickupTime())
                    .status(order.getStatus())
                    .items(itemDtos)
                    .build();
        }).toList();
    }



    public OrderResponseDto acceptOrder(String spUserId, String orderId) {
        String lockKey = LOCK_PREFIX + orderId;

        if (!tryAcquireLock(lockKey)) {
            throw new IllegalStateException("Order is currently being processed by another request.");
        }

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " not found"));

            ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                    .orElseThrow(() -> new IllegalStateException("Service Provider not found for user: " + spUserId));

            if (!order.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
                throw new IllegalStateException("Order does not belong to logged-in service provider");
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("Order is not in PENDING status and cannot be accepted");
            }

            // Update order status
            order.setStatus(OrderStatus.ACCEPTED_BY_PROVIDER);
            order = orderRepository.save(order);
            saveOrderStatusHistory(order, OrderStatus.ACCEPTED_BY_PROVIDER);

            // Remove from Redis pending set
            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());

            // Send notifications
//            smsService.sendOrderStatusNotification(order.getContactPhone(),
//                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");
            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
                    "Order Accepted",
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");

            // Assign to delivery agent if selected
            if (Boolean.FALSE.equals(sp.getNeedOfDeliveryAgent())) {
                deliveryService.calculateDeliveryChargeForProvider(order);
                orderRepository.save(order); // Save totalKm
            } else {
                // Assign to delivery agent if needed
                deliveryService.assignToDeliveryAgentCustomerOrders(order.getOrderId());
            }
            // Handle delivery logic through SMS
//            if (sp.getNeedOfDeliveryAgent() != null && sp.getNeedOfDeliveryAgent()) {
//
//                // Customer wants pickup from home
//                deliveryService.assignToDeliveryAgent(order.getOrderId());
//                orderOtpService.generateAndSendOtp(order, order.getUsers(), null, OtpPurpose.PICKUP_CUSTOMER, order.getContactPhone());
//            } else {
//                // Provider arranges pickup; OTP will be used for provider-side pickup verification
//                orderOtpService.generateAndSendOtp(order, order.getUsers(), null, OtpPurpose.PICKUP_CUSTOMER, order.getContactPhone());
//            }
            // Handle delivery logic through EMAIL
            if (sp.getNeedOfDeliveryAgent() != null && sp.getNeedOfDeliveryAgent()) {

                // Customer wants pickup from home
                deliveryService.assignToDeliveryAgentCustomerOrders(order.getOrderId());
                orderEmailOtpService.generateAndSendOtp(order, order.getUsers(), null, OtpPurpose.PICKUP_CUSTOMER, order.getUsers().getEmail());
            } else {
                // Provider arranges pickup; OTP will be used for provider-side pickup verification
                orderEmailOtpService.generateAndSendOtp(order, order.getUsers(), null, OtpPurpose.PICKUP_CUSTOMER, order.getUsers().getEmail());
            }

            log.info("Order {} accepted by service provider {} for customer {}",
                    order.getOrderId(), spUserId, order.getUsers().getUserId());

            return orderMapper.toOrderResponseDto(order);

        } catch (Exception e) {
            log.error("Failed to accept order {} by service provider {}: {}",
                    orderId, spUserId, e.getMessage(), e);
            throw new RuntimeException("Failed to accept order: " + e.getMessage());
        } finally {
            releaseLock(lockKey);
        }
    }


    public void rejectOrder(String spUserId, String orderId) {
        String lockKey = LOCK_PREFIX + orderId;

        if (!tryAcquireLock(lockKey)) {
            throw new IllegalStateException("Order is currently being processed by another request.");
        }

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " not found"));

            ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                    .orElseThrow(() -> new IllegalStateException("Service Provider not found for user: " + spUserId));

            if (!order.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
                throw new IllegalStateException("Order does not belong to logged-in service provider");
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("Order is not in PENDING status and cannot be rejected");
            }

            order.setStatus(OrderStatus.REJECTED_BY_PROVIDER);
            orderRepository.save(order);

            saveOrderStatusHistory(order, OrderStatus.REJECTED_BY_PROVIDER);

            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());

            smsService.sendOrderStatusNotification(order.getContactPhone(),
                    "We're sorry! Your order " + order.getOrderId() + " has been rejected.");
            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
                    "Order Rejected",
                    "We're sorry! Your order " + order.getOrderId() + " has been rejected.");

            log.info("Order {} rejected by service provider {} for customer {}", order.getOrderId(), spUserId, order.getUsers().getUserId());

        } catch (Exception e) {
            log.error("Failed to reject order {} by service provider {}: {}", orderId, spUserId, e.getMessage(), e);
            throw e;
        } finally {
            releaseLock(lockKey);
        }
    }


    public void markOrderInCleaning(String spUserId, String orderId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new EntityNotFoundException("Service Provider not found"));

        if (!order.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
            throw new AccessDeniedException("Unauthorized to update this order");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED_BY_PROVIDER) {
            throw new IllegalStateException("Order must be ACCEPTED to mark as IN_CLEANING");
        }

        order.setStatus(OrderStatus.IN_CLEANING);
        orderRepository.save(order);

        saveOrderStatusHistory(order, OrderStatus.IN_CLEANING);
    }

    public void markOrderReadyForDelivery(String spUserId, String orderId) throws AccessDeniedException, JsonProcessingException {
        Order order = orderRepository.findByorderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new EntityNotFoundException("Service Provider not found"));

        if (!order.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
            throw new AccessDeniedException("Unauthorized to update this order");
        }

        if (order.getStatus() != OrderStatus.IN_CLEANING) {
            throw new IllegalStateException("Order must be IN_CLEANING to mark as READY_FOR_DELIVERY");
        }

        order.setStatus(OrderStatus.READY_FOR_DELIVERY);
        order.setDeliveryDate(LocalDate.now());

        saveOrderStatusHistory(order, OrderStatus.READY_FOR_DELIVERY);
        orderRepository.save(order);
        saveOrderStatusHistory(order, OrderStatus.READY_FOR_DELIVERY);
        //For SMS
//        if (Boolean.TRUE.equals(sp.getNeedOfDeliveryAgent())) {
//            // Agent will come to collect clothes from provider —> send OTP to provider
//        deliveryService.assignToDeliveryAgentServiceProviderOrders(order.getOrderId());
//            orderOtpService.generateAndSendOtp(
//                    order,
//                    sp.getUser(),
//                    null,
//                    OtpPurpose.CONFIRM_FOR_CLOTHS,
//                    sp.getUser().getPhoneNo()
//
//            );
//        } else {
//            // Provider will deliver directly to customer —> send delivery OTP to customer
//            orderOtpService.generateAndSendOtp(
//                    order,
//                    order.getUsers(),
//                    null,
//                    OtpPurpose.DELIVERY_CUSTOMER,
//                    order.getUsers().getPhoneNo()
//            );
//        }
        //For Email
        if (Boolean.TRUE.equals(sp.getNeedOfDeliveryAgent())) {
            deliveryService.assignToDeliveryAgentServiceProviderOrders(order.getOrderId());
            orderEmailOtpService.generateAndSendOtp(
                    order,
                    sp.getUser(), // provider user
                    null,
                    OtpPurpose.CONFIRM_FOR_CLOTHS,
                    sp.getUser().getEmail()
            );
        } else {
            // Provider will deliver directly to customer —> send delivery OTP to customer
            orderEmailOtpService.generateAndSendOtp(
                    order,
                    order.getUsers(),
                    null,
                    OtpPurpose.DELIVERY_CUSTOMER,
                    order.getUsers().getEmail()
            );
        }
    }

    private void saveOrderStatusHistory(Order order, OrderStatus status) {
        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(status)
                .changedAt(LocalDateTime.now())
                .build());
    }
    public void respondToFeedbackByUserId(String spUserId, Long feedbackId, String responseMessage) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found for user: " + spUserId));

        FeedbackProviders feedback = feedbackProvidersRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
            throw new RuntimeException("Unauthorized: Feedback does not belong to this service provider");
        }

        feedback.setResponse(responseMessage);
        feedbackProvidersRepository.save(feedback);

        log.info("Service Provider {} responded to feedback {} with message: {}",
                sp.getServiceProviderId(), feedbackId, responseMessage);
    }

    public List<FeedbackResponseDto> getFeedbackForServiceProvider(String providerId) {
        List<FeedbackProviders> feedbackList =
                feedbackProvidersRepository.findByServiceProvider_ServiceProviderIdOrderByCreatedAtDesc(providerId);

        return feedbackList.stream().map(fb -> FeedbackResponseDto.builder()
                .customerName(fb.getUser().getName())
                .review(fb.getReview())
                .rating(fb.getRating())
                .submittedAt(fb.getCreatedAt())
                .build()
        ).collect(Collectors.toList());
    }


    public List<OrderHistoryDto> getOrderHistoryForProvider(String providerId, String statusStr) {
        OrderStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = OrderStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status");
            }
        }

        List<Order> orders = (status != null)
                ? orderRepository.findByServiceProvider_ServiceProviderIdAndStatus(providerId, status)
                : orderRepository.findByServiceProvider_ServiceProviderId(providerId);

        List<OrderHistoryDto> history = new ArrayList<>();

        for (Order order : orders) {
            List<OrderHistoryDto.ItemDetail> items = new ArrayList<>();

            for (BookingItem item : order.getBookingItems()) {
                items.add(OrderHistoryDto.ItemDetail.builder()
                        .serviceName(item.getItem().getService() != null ? item.getItem().getService().getServiceName() : null)
                        .subServiceName(item.getItem().getSubService() != null ? item.getItem().getSubService().getSubServiceName() : null)
                        .itemName(item.getItem().getItemName())
                        .quantity(item.getQuantity())
                        .build());
            }

            history.add(OrderHistoryDto.builder()
                    .orderId(order.getOrderId())
                    .status(order.getStatus().name())
                    .items(items)
                    .build());
        }

        return history;
    }

}
