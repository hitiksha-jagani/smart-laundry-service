package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.Customer.TicketResponseDto;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.OrderService;
import com.SmartLaundry.service.Customer.SMSService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceProviderOrderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ServiceProviderRepository serviceProviderRepository;
    private final SMSService smsService;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final OrderService orderService;
    @Autowired
    private final OrderRepository orderRepository;
    private final FeedbackProvidersRepository feedbackProvidersRepository;
    private static final Logger log = LoggerFactory.getLogger(ServiceProviderOrderService.class);
    private final TicketRepository ticketRepository;
    private final FAQRepository faqRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final FeedbackAgentsRepository feedbackAgentsRepository;

    private static final long LOCK_EXPIRY_MILLIS = 10_000;
    private static final String LOCK_PREFIX = "lock:order:user:";

    // If Redis stores orders by orderId now:
    private String getRedisKey(String orderId) {
        return "order:id:" + orderId;
    }

    // The pending orders set key remains the same since it is still based on serviceProviderId
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

            order.setStatus(OrderStatus.ACCEPTED);
            order = orderRepository.save(order);

            // Remove from any Redis pending sets if used
            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());

            // Notifications
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

            // Remove from Redis pending sets if applicable
            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(sp.getServiceProviderId()), order.getOrderId());

            // Notifications
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

    // Existing method expecting serviceProviderId directly
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

    //Delivery Agent
    public void respondToAgentFeedbackByUserId(String agentUserId, Long feedbackId, String responseMessage) {
        // Find the DeliveryAgent by the userId (assuming you have a method in your repository)
        DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(agentUserId)
                .orElseThrow(() -> new RuntimeException("Delivery agent not found with userId: " + agentUserId));


        // Find the FeedbackAgents by feedbackId
        FeedbackAgents feedback = feedbackAgentsRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        // Check if the feedback belongs to this agent
        if (!feedback.getAgent().getDeliveryAgentId().equals(agent.getDeliveryAgentId())) {
            throw new RuntimeException("Unauthorized: Feedback does not belong to this delivery agent");
        }

        // Set the response message and save
        feedback.setResponse(responseMessage);
        feedbackAgentsRepository.save(feedback);

        log.info("Delivery Agent {} responded to feedback {} with message: {}",
                agent.getDeliveryAgentId(), feedbackId, responseMessage);
    }


    public TicketResponseDto respondToTicket(Long ticketId, TicketResponseDto dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setResponse(dto.getResponseText());
        ticket.setStatus("RESPONDED");
        ticket.setRespondedAt(LocalDateTime.now());

        ticketRepository.save(ticket);

        // Create FAQ from ticket
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
