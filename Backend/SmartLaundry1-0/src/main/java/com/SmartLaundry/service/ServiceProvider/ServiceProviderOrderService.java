
package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.Customer.TicketResponseDto;
import com.SmartLaundry.dto.ServiceProvider.ActiveOrderDto;
import com.SmartLaundry.dto.ServiceProvider.FeedbackResponseDto;
import com.SmartLaundry.dto.ServiceProvider.OrderHistoryDto;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.Customer.SMSService;
import com.SmartLaundry.service.DeliveryAgent.DeliveriesService;
import jakarta.persistence.EntityNotFoundException;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final ServiceProviderRepository serviceProviderRepository;
    private final SMSService smsService;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final FeedbackProvidersRepository feedbackProvidersRepository;
    private final TicketRepository ticketRepository;
    private final FAQRepository faqRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final FeedbackAgentsRepository feedbackAgentsRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final DeliveriesService deliveryService;
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


    public List<ActiveOrderDto> getActiveOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new IllegalStateException("Service Provider not found"));

        List<Order> activeOrders = orderRepository.findByServiceProviderAndStatus(sp, OrderStatus.IN_CLEANING);

        return activeOrders.stream().flatMap(order ->
                order.getBookingItems().stream().map(item -> {
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
                            .orderId(order.getOrderId())
                            .service(serviceName)
                            .subService(subServiceName)
                            .itemName(itemEntity.getItemName())
                            .quantity(item.getQuantity())
                            .pickupDate(order.getPickupDate())
                            .pickupTime(order.getPickupTime())
                            .status(order.getStatus())
                            .build();
                })
        ).collect(Collectors.toList());
    }

    public List<ActiveOrderDto> getPendingOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new IllegalStateException("Service Provider not found"));

        List<Order> pendingOrders = orderRepository.findByServiceProviderAndStatus(sp, OrderStatus.PENDING);

        return pendingOrders.stream().flatMap(order ->
                order.getBookingItems().stream().map(item -> {
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
                            .orderId(order.getOrderId())
                            .service(serviceName)
                            .subService(subServiceName)
                            .itemName(itemEntity.getItemName())
                            .quantity(item.getQuantity())
                            .pickupDate(order.getPickupDate())
                            .pickupTime(order.getPickupTime())
                            .status(order.getStatus())
                            .build();
                })
        ).collect(Collectors.toList());
    }


//    public OrderResponseDto acceptOrder(String spUserId, String orderId, boolean needOfDeliveryAgent) {
//        String lockKey = LOCK_PREFIX + orderId;
//
//        if (!tryAcquireLock(lockKey)) {
//            throw new IllegalStateException("Order is currently being processed by another request.");
//        }
//
//        try {
//            // Fetch the order
//            Order order = orderRepository.findById(orderId)
//                    .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " not found"));
//
//            // Validate service provider
//            ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
//                    .orElseThrow(() -> new IllegalStateException("Service Provider not found for user: " + spUserId));
//
//            if (!order.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
//                throw new IllegalStateException("Order does not belong to logged-in service provider");
//            }
//
//            if (order.getStatus() != OrderStatus.PENDING) {
//                throw new IllegalStateException("Order is not in PENDING status and cannot be accepted");
//            }
//
//            // Update order status and needOfDeliveryAgent BEFORE saving
//            order.setStatus(OrderStatus.ACCEPTED_BY_PROVIDER);
//            order.setNeedOfDeliveryAgent(needOfDeliveryAgent);
//            order = orderRepository.save(order); // Save with updated fields
//
//            // Save status history
//            saveOrderStatusHistory(order, OrderStatus.ACCEPTED_BY_PROVIDER);
//
//            // Remove from Redis pending set
//            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());
//
//            // Send notifications
//            smsService.sendOrderStatusNotification(order.getContactPhone(),
//                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");
//            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
//                    "Order Accepted",
//                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");
//
//            // Assign to delivery agent if selected
//            if (needOfDeliveryAgent) {
//                deliveryService.assignToDeliveryAgent(order.getOrderId());
//            }
//
//            log.info("Order {} accepted by service provider {} for customer {}",
//                    order.getOrderId(), spUserId, order.getUsers().getUserId());
//
//            // Return response
//            return orderMapper.toOrderResponseDto(order);
//
//        } catch (Exception e) {
//            log.error("Failed to accept order {} by service provider {}: {}",
//                    orderId, spUserId, e.getMessage(), e);
//            throw new RuntimeException("Failed to accept order: " + e.getMessage());
//        } finally {
//            releaseLock(lockKey);
//        }
//    }


    public OrderResponseDto acceptOrder(String spUserId, String orderId) {
        String lockKey = LOCK_PREFIX + orderId;

        if (!tryAcquireLock(lockKey)) {
            throw new IllegalStateException("Order is currently being processed by another request.");
        }

        try {
            // Fetch the order
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " not found"));

            // Validate service provider
            ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                    .orElseThrow(() -> new IllegalStateException("Service Provider not found for user: " + spUserId));

            if (!order.getServiceProvider().getServiceProviderId().equals(sp.getServiceProviderId())) {
                throw new IllegalStateException("Order does not belong to logged-in service provider");
            }

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new IllegalStateException("Order is not in PENDING status and cannot be accepted");
            }

            // Update order status and needOfDeliveryAgent BEFORE saving
            order.setStatus(OrderStatus.ACCEPTED_BY_PROVIDER);
//            order.setNeedOfDeliveryAgent(needOfDeliveryAgent);
            order = orderRepository.save(order); // Save with updated fields

            // Save status history
            saveOrderStatusHistory(order, OrderStatus.ACCEPTED_BY_PROVIDER);

            // Remove from Redis pending set
            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());

            // Send notifications
            smsService.sendOrderStatusNotification(order.getContactPhone(),
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");
            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
                    "Order Accepted",
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");

            // Assign to delivery agent if selected
            if (sp.getNeedOfDeliveryAgent().equals(true)) {
                deliveryService.assignToDeliveryAgent(order.getOrderId());
            }

            log.info("Order {} accepted by service provider {} for customer {}",
                    order.getOrderId(), spUserId, order.getUsers().getUserId());

            // Return response
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

    public void markOrderReadyForDelivery(String spUserId, String orderId) throws AccessDeniedException {
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
        orderRepository.save(order);
        saveOrderStatusHistory(order, OrderStatus.READY_FOR_DELIVERY);
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
