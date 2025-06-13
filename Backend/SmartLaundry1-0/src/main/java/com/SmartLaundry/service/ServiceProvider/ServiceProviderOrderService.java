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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
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

            order.setStatus(OrderStatus.ACCEPTED_BY_PROVIDER);
            order = orderRepository.save(order);

            orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.ACCEPTED_BY_PROVIDER)
                    .changedAt(LocalDateTime.now())
                    .build());

            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());

            smsService.sendOrderStatusNotification(order.getContactPhone(),
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");
            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
                    "Order Accepted",
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");

            log.info("Order {} accepted by service provider {} for customer {}", order.getOrderId(), spUserId, order.getUsers().getUserId());

            return orderMapper.toOrderResponseDto(order);

        } catch (Exception e) {
            log.error("Failed to accept order {} by service provider {}: {}", orderId, spUserId, e.getMessage(), e);
            throw e;
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

            order.setStatus(OrderStatus.REJECTED);
            orderRepository.save(order);

            orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                    .order(order)
                    .status(OrderStatus.REJECTED)
                    .changedAt(LocalDateTime.now())
                    .build());

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

        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.IN_CLEANING)
                .changedAt(LocalDateTime.now())
                .build());
    }

    public List<ActiveOrderDto> getActiveOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new IllegalStateException("Service Provider not found"));

        List<Order> activeOrders = orderRepository.findByServiceProviderAndStatus(sp, OrderStatus.IN_CLEANING);

        return activeOrders.stream().flatMap(order ->
                order.getBookingItems().stream().map(item -> ActiveOrderDto.builder()
                        .orderId(order.getOrderId())
                        .service(item.getItem().getService().getServiceName())
                        .subService(item.getItem().getSubService().getSubServiceName())
                        .itemName(item.getItem().getItemName())
                        .quantity(item.getQuantity())
                        .pickupDate(order.getPickupDate())
                        .pickupTime(order.getPickupTime())
                        .status(order.getStatus())
                        .build()
                )
        ).collect(Collectors.toList());
    }

    public List<OrderResponseDto> getPendingOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found for user: " + spUserId));
        String serviceProviderId = sp.getServiceProviderId();

        Set<Object> orderIds = redisTemplate.opsForSet().members(getPendingOrdersSetKey(serviceProviderId));
        if (orderIds == null || orderIds.isEmpty()) return Collections.emptyList();

        List<OrderResponseDto> pendingOrders = new ArrayList<>();

        for (Object orderIdObj : orderIds) {
            String orderId = (String) orderIdObj;
            String redisKey = getRedisKey(orderId);
            Map<Object, Object> data = redisTemplate.opsForHash().entries(redisKey);
            if (data == null || data.isEmpty()) continue;
            if (!"PENDING".equalsIgnoreCase((String) data.get("status"))) continue;

            OrderResponseDto dto = orderService.buildOrderResponseDtoFromRedisData(orderId, data);
            pendingOrders.add(dto);
        }
        return pendingOrders;
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

        List<Order> orders = orderRepository.findByServiceProvider_ServiceProviderIdAndStatus(providerId, status);

        List<OrderHistoryDto> history = new ArrayList<>();
        for (Order order : orders) {
            for (BookingItem item : order.getBookingItems()) {
                history.add(OrderHistoryDto.builder()
                        .orderId(order.getOrderId())
                        .serviceName(item.getItem().getService().getServiceName())
                        .subServiceName(item.getItem().getSubService().getSubServiceName())
                        .itemName(item.getItem().getItemName())
                        .quantity(item.getQuantity())
                        .status(order.getStatus().name())
                        .build());
            }
        }

        return history;
    }

//    public void respondToAgentFeedbackByUserId(String agentUserId, Long feedbackId, String responseMessage) {
//        DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(agentUserId)
//                .orElseThrow(() -> new RuntimeException("Delivery agent not found with userId: " + agentUserId));
//
//        FeedbackAgents feedback = feedbackAgentsRepository.findById(feedbackId)
//                .orElseThrow(() -> new RuntimeException("Feedback not found"));
//
//        if (!feedback.getDeliveryAgent().getDeliveryAgentId().equals(agent.getDeliveryAgentId())) {
//            throw new RuntimeException("Unauthorized: Feedback does not belong to this delivery agent");
//        }
//
//        feedback.setResponse(responseMessage);
//        feedbackAgentsRepository.save(feedback);
//
//        log.info("Delivery Agent {} responded to feedback {} with message: {}",
//                agent.getDeliveryAgentId(), feedbackId, responseMessage);
//    }

    public TicketResponseDto respondToTicket(Long ticketId, TicketResponseDto dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setResponse(dto.getResponseText());
        ticket.setStatus(TicketStatus.RESPONDED);
        ticket.setRespondedAt(LocalDateTime.now());

        ticketRepository.save(ticket);

        FAQ faq = FAQ.builder()
                .ticket(ticket)
                .visibilityStatus(dto.isMakeFaqVisible())
                .question(ticket.getTitle())
                .answer(dto.getResponseText())
                .category(ticket.getCategory() != null ? ticket.getCategory() : "General")
                .build();

        faqRepository.save(faq);

        return dto;
    }
}
