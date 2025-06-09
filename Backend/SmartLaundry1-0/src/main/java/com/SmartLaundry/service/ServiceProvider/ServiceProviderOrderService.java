package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.repository.BookingItemRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.UserRepository;
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
    private static final Logger log = LoggerFactory.getLogger(ServiceProviderOrderService.class);

    private static final long LOCK_EXPIRY_MILLIS = 10_000;
    private static final String LOCK_PREFIX = "lock:order:user:";

    private String getRedisKey(String userId) {
        return "order:user:" + userId;
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

    public OrderResponseDto acceptOrder(String spUserId, String customerUserId) {
        String lockKey = LOCK_PREFIX + customerUserId;

        if (!tryAcquireLock(lockKey)) {
            throw new IllegalStateException("Order is currently being processed by another request.");
        }

        try {
            String redisKey = getRedisKey(customerUserId);
            Map<Object, Object> data = redisTemplate.opsForHash().entries(redisKey);

            if (data.isEmpty() || !"PENDING".equalsIgnoreCase((String) data.get("status"))) {
                throw new IllegalStateException("No pending order found in Redis for user: " + customerUserId);
            }

            String orderSpId = (String) data.get("serviceProviderId");
            ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                    .orElseThrow(() -> new IllegalStateException("Service Provider not found for user: " + spUserId));

            if (orderSpId == null || !orderSpId.equals(sp.getServiceProviderId())) {
                throw new IllegalStateException("Order does not belong to logged-in service provider");
            }

            String orderId = (String) data.get("orderId");

            Order order;
            if (orderId != null && orderRepository.existsById(orderId)) {
                // Fetch and update existing order
                order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " not found"));
                order.setStatus(OrderStatus.ACCEPTED);
            } else {
                // Build new order from Redis with ACCEPTED status
                order = orderService.buildOrderFromRedis(customerUserId, OrderStatus.ACCEPTED);
            }

            // Save order once here
            order = orderRepository.save(order);

            // Clean up Redis
            redisTemplate.delete(redisKey);
            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(orderSpId), customerUserId);

            // Notifications
            smsService.sendOrderStatusNotification(order.getContactPhone(),
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");
            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
                    "Order Accepted",
                    "Your LaundryService Order " + order.getOrderId() + " is Accepted");

            log.info("Order {} accepted by service provider {} for customer {}", order.getOrderId(), spUserId, customerUserId);

            return orderMapper.toOrderResponseDto(order);

        } catch (Exception e) {
            log.error("Failed to accept order for customer {} by service provider {}: {}", customerUserId, spUserId, e.getMessage(), e);
            throw e;
        } finally {
            releaseLock(lockKey);
        }
    }



    public void rejectOrder(String spUserId, String customerUserId) {
        String lockKey = LOCK_PREFIX + customerUserId;

        if (!tryAcquireLock(lockKey)) {
            throw new IllegalStateException("Order is currently being processed by another request.");
        }

        try {
            String redisKey = getRedisKey(customerUserId);
            Map<Object, Object> data = redisTemplate.opsForHash().entries(redisKey);

            if (data.isEmpty() || !"PENDING".equalsIgnoreCase((String) data.get("status"))) {
                throw new IllegalStateException("No pending order found in Redis for user: " + customerUserId);
            }

            String orderSpId = (String) data.get("serviceProviderId");
            ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                    .orElseThrow(() -> new IllegalStateException("Service Provider not found for user: " + spUserId));

            if (orderSpId == null || !orderSpId.equals(sp.getServiceProviderId())) {
                throw new IllegalStateException("Order does not belong to logged-in service provider");
            }

            String orderId = (String) data.get("orderId");
            Order order;

            if (orderId != null && orderRepository.existsById(orderId)) {
                // Fetch existing order from DB and update status
                order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalStateException("Order with ID " + orderId + " not found"));
                order.setStatus(OrderStatus.REJECTED);
            } else {
                // Build new order from Redis with REJECTED status (unsaved)
                order = orderService.buildOrderFromRedis(customerUserId, OrderStatus.REJECTED);
            }

            // Save the order once here
            orderRepository.save(order);

            // Clean up Redis data after save
            redisTemplate.delete(redisKey);
            redisTemplate.opsForSet().remove(getPendingOrdersSetKey(orderSpId), customerUserId);

            // Notify customer
            smsService.sendOrderStatusNotification(order.getContactPhone(),
                    "We're sorry! Your order " + order.getOrderId() + " has been rejected.");

            emailService.sendOrderStatusNotification(order.getUsers().getEmail(),
                    "Order Rejected",
                    "We're sorry! Your order " + order.getOrderId() + " has been rejected.");

            log.info("Order {} rejected by service provider {} for customer {}", order.getOrderId(), spUserId, customerUserId);

        } catch (Exception e) {
            log.error("Failed to reject order for customer {} by service provider {}: {}", customerUserId, spUserId, e.getMessage(), e);
            throw e;
        } finally {
            releaseLock(lockKey);
        }
    }





    public List<OrderResponseDto> getPendingOrdersForServiceProvider(String spUserId) {
        ServiceProvider sp = serviceProviderRepository.findByUserUserId(spUserId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found for user: " + spUserId));
        String serviceProviderId = sp.getServiceProviderId();

        Set<Object> customerUserIds = redisTemplate.opsForSet().members(getPendingOrdersSetKey(serviceProviderId));
        if (customerUserIds == null || customerUserIds.isEmpty()) return Collections.emptyList();

        List<OrderResponseDto> pendingOrders = new ArrayList<>();

        for (Object uidObj : customerUserIds) {
            String customerUserId = (String) uidObj;
            String redisKey = getRedisKey(customerUserId);
            Map<Object, Object> data = redisTemplate.opsForHash().entries(redisKey);
            if (data == null || data.isEmpty()) continue;
            if (!"PENDING".equalsIgnoreCase((String) data.get("status"))) continue;

            OrderResponseDto dto = orderService.buildOrderResponseDtoFromRedisData(customerUserId, data);
            pendingOrders.add(dto);
        }
        return pendingOrders;
    }
}
