package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.*;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderBookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ItemsRepository itemsRepository;
    private final PriceRepository priceRepository;
    private final BookingItemRepository bookingItemRepository;
    private final OrderSchedulePlanRepository orderSchedulePlanRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final SMSService smsService;
    private final EmailService emailService;
    //private final ServiceProviderOrderService spOrderService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private String getRedisKey(String userId) {
        return "order:user:" + userId;
    }


    // Save initial order details in Redis
    public void saveInitialOrderDetails(String userId, BookOrderRequestDto dto) {
        if (dto == null || dto.getServiceProviderId() == null || dto.getPickupDate() == null ||
                dto.getPickupTime() == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Missing required fields in BookOrderRequestDto");
        }

        String key = getRedisKey(userId);

        redisTemplate.opsForHash().put(key, "serviceProviderId", dto.getServiceProviderId());
        redisTemplate.opsForHash().put(key, "pickupDate", dto.getPickupDate().toString());
        redisTemplate.opsForHash().put(key, "pickupTime", dto.getPickupTime().toString());
        redisTemplate.opsForHash().put(key, "goWithSchedulePlan", String.valueOf(dto.isGoWithSchedulePlan()));
        try {
            redisTemplate.opsForHash().put(key, "items", objectMapper.writeValueAsString(dto.getItems()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize items list", e);
        }
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    // Save schedule plan details in Redis
    public void saveSchedulePlan(String userId, SchedulePlanRequestDto dto) {
        if (dto == null || dto.getSchedulePlan() == null) {
            throw new IllegalArgumentException("Schedule plan or selection is missing");
        }

        String key = getRedisKey(userId);

        redisTemplate.opsForHash().put(key, "schedulePlan", dto.getSchedulePlan().name());
        redisTemplate.opsForHash().put(key, "payEachDelivery", String.valueOf(dto.isPayEachDelivery()));
        redisTemplate.opsForHash().put(key, "payLastDelivery", String.valueOf(dto.isPayLastDelivery()));
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    // Save contact info in Redis
    public void saveContactInfo(String userId, ContactDetailsDto dto) {
        if (dto == null || dto.getContactName() == null || dto.getContactPhone() == null ||
                dto.getContactAddress() == null || dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new IllegalArgumentException("Missing required contact fields");
        }

        String key = getRedisKey(userId);

        redisTemplate.opsForHash().put(key, "contactName", dto.getContactName());
        redisTemplate.opsForHash().put(key, "contactPhone", dto.getContactPhone());
        redisTemplate.opsForHash().put(key, "contactAddress", dto.getContactAddress());
        redisTemplate.opsForHash().put(key, "latitude", String.valueOf(dto.getLatitude()));
        redisTemplate.opsForHash().put(key, "longitude", String.valueOf(dto.getLongitude()));
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    // Validate that all required fields exist in Redis before order placement
    public void validateRedisOrderData(String userId) {
        Map<Object, Object> data = redisTemplate.opsForHash().entries(getRedisKey(userId));
        if (data.isEmpty())
            throw new IllegalStateException("No order data found for user: " + userId);

        List<String> requiredFields = List.of(
                "serviceProviderId", "pickupDate", "pickupTime",
                "contactName", "contactPhone", "contactAddress",
                "latitude", "longitude", "items"
        );
        for (String field : requiredFields) {
            if (!data.containsKey(field) || data.get(field) == null) {
                throw new IllegalStateException("Missing required field '" + field + "' in Redis order data");
            }
        }
    }

    // Place order - mark status PENDING and notify service provider
    public void placeOrder(String userId) {
        validateRedisOrderData(userId);

        String key = getRedisKey(userId);
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        if (data.isEmpty()) throw new IllegalStateException("No order data found to place order");

        // Set status to PENDING in Redis
        redisTemplate.opsForHash().put(key, "status", "PENDING");
        redisTemplate.expire(key, Duration.ofDays(7));

        String spId = (String) data.get("serviceProviderId");

        // Add userId to pending orders set of service provider
        redisTemplate.opsForSet().add(ServiceProviderOrderService.getPendingOrdersSetKey(spId), userId);

        // Notify Service Provider (via SMS and Email)
        ServiceProvider sp = serviceProviderRepository.findById(spId)
                .orElseThrow(() -> new IllegalArgumentException("Service provider not found: " + spId));

        smsService.sendOrderStatusNotification(
                sp.getUser().getPhoneNo(),
                "New laundry order request pending from user " + userId + ". Please accept or reject."
        );
        emailService.sendOrderStatusNotification(
                sp.getUser().getEmail(),
                "New Laundry Order Request",
                "You have a new laundry order request pending from user " + userId + ". Please log in to accept or reject."
        );
    }

    // Build order response DTO from Redis stored data
    public  OrderResponseDto buildOrderResponseDtoFromRedisData(String userId, Map<Object, Object> data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("No Redis data found for user: " + userId);
        }

        OrderResponseDto.OrderResponseDtoBuilder dtoBuilder = OrderResponseDto.builder();

        dtoBuilder.orderId("PENDING:" + userId); // temp id
        dtoBuilder.userId(userId);
        dtoBuilder.serviceProviderId((String) data.get("serviceProviderId"));

        // Pickup date/time parsing
        String pickupDateStr = (String) data.get("pickupDate");
        if (pickupDateStr != null) {
            dtoBuilder.pickupDate(LocalDate.parse(pickupDateStr));
        }
        String pickupTimeStr = (String) data.get("pickupTime");
        if (pickupTimeStr != null) {
            dtoBuilder.pickupTime(LocalTime.parse(pickupTimeStr));
        }

        // Contact info
        dtoBuilder.contactName((String) data.get("contactName"));
        dtoBuilder.contactPhone((String) data.get("contactPhone"));
        dtoBuilder.contactAddress((String) data.get("contactAddress"));

        // Latitude and Longitude parsing
        String latStr = (String) data.get("latitude");
        if (latStr != null) {
            dtoBuilder.latitude(Double.parseDouble(latStr));
        }
        String lonStr = (String) data.get("longitude");
        if (lonStr != null) {
            dtoBuilder.longitude(Double.parseDouble(lonStr));
        }

        dtoBuilder.status(OrderStatus.PENDING);

        // Parse booking items JSON into BookingItemDto list
        String itemsJson = (String) data.get("items");
        List<OrderResponseDto.BookingItemDto> bookingItems = new ArrayList<>();
        if (itemsJson != null) {
            try {
                List<OrderItemRequest> orderItems = objectMapper.readValue(itemsJson, new TypeReference<List<OrderItemRequest>>() {});
                for (OrderItemRequest itemReq : orderItems) {
                    String itemId = itemReq.getItemId();
                    Integer quantity = itemReq.getQuantity();

                    Items item = itemsRepository.findById(itemId).orElse(null);
                    Price price = null;
                    if (item != null) {
                        ServiceProvider sp = serviceProviderRepository.findById((String) data.get("serviceProviderId")).orElse(null);
                        if (sp != null) {
                            price = priceRepository.findByServiceProviderAndItem(sp, item).orElse(null);
                        }
                    }

                    Double finalPrice = (price != null && quantity != null) ? price.getPrice().doubleValue() * quantity : null;

                    bookingItems.add(OrderResponseDto.BookingItemDto.builder()
                            .itemId(itemId)
                            .itemName(item != null ? item.getItemName() : null)
                            .quantity(quantity)
                            .finalPrice(finalPrice)
                            .build());
                }
            } catch (Exception e) {
                log.error("Error parsing items JSON for user " + userId, e);
            }
        }
        dtoBuilder.bookingItems(bookingItems);

        // Schedule plan if present
        String planStr = (String) data.get("schedulePlan");
        if (planStr != null) {
            boolean payEach = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payEachDelivery", "false")));
            boolean payLast = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payLastDelivery", "false")));
            dtoBuilder.schedulePlan(OrderResponseDto.SchedulePlanDto.builder()
                    .plan(planStr)
                    .payEachDelivery(payEach)
                    .payLastDelivery(payLast)
                    .build());
        } else {
            dtoBuilder.schedulePlan(null);
        }

        return dtoBuilder.build();
    }

    // Finalize and create actual Order entity from Redis and save to DB
    public OrderResponseDto createOrder(String userId) {
        validateRedisOrderData(userId);

        // 1. Build Order from Redis (with all necessary in-memory objects attached)
        Order order = buildOrderFromRedis(userId, OrderStatus.PENDING);

        // 2. Save the Order first
        order = orderRepository.save(order);  // This assigns a valid ID to Order

        // 3. Save OrderSchedulePlan if present
        if (order.getOrderSchedulePlan() != null) {
            orderSchedulePlanRepository.save(order.getOrderSchedulePlan());
        }

        // 4. Clean up Redis
        redisTemplate.delete(getRedisKey(userId));
        redisTemplate.opsForSet().remove(
                ServiceProviderOrderService.getPendingOrdersSetKey(order.getServiceProvider().getServiceProviderId()),
                userId
        );

        // 5. Return response DTO
        return orderMapper.toOrderResponseDto(order);
    }



@Transactional
public Order buildOrderFromRedis(String userId, OrderStatus status) {
    String redisKey = getRedisKey(userId);
    Map<Object, Object> data = redisTemplate.opsForHash().entries(redisKey);
    if (data.isEmpty()) {
        throw new IllegalStateException("No order data found for user: " + userId);
    }

    // Fetch user
    Users user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    // Fetch service provider
    String spId = (String) data.get("serviceProviderId");
    if (spId == null || spId.isBlank()) {
        throw new IllegalStateException("Service Provider ID missing in Redis order data");
    }
    ServiceProvider sp = serviceProviderRepository.findById(spId)
            .orElseThrow(() -> new IllegalArgumentException("Service provider not found: " + spId));

    try {
        // Parse required fields from Redis
        String pickupDateStr = (String) data.get("pickupDate");
        String pickupTimeStr = (String) data.get("pickupTime");
        String contactName = (String) data.get("contactName");
        String contactPhone = (String) data.get("contactPhone");
        String contactAddress = (String) data.get("contactAddress");

        if (pickupDateStr == null || pickupTimeStr == null || contactName == null ||
                contactPhone == null || contactAddress == null) {
            throw new IllegalStateException("Missing required order details in Redis");
        }

        LocalDate pickupDate = LocalDate.parse(pickupDateStr);
        LocalTime pickupTime = LocalTime.parse(pickupTimeStr);

        double latitude = 0.0;
        double longitude = 0.0;
        try {
            String latStr = (String) data.get("latitude");
            String lonStr = (String) data.get("longitude");
            if (latStr != null) latitude = Double.parseDouble(latStr);
            if (lonStr != null) longitude = Double.parseDouble(lonStr);
        } catch (NumberFormatException ignored) {}

        Order order = new Order();

        // Restore orderId from Redis if present
        String orderId = (String) data.get("orderId");
        if (orderId != null && !orderId.isBlank()) {
            order.setOrderId(orderId);
        }

        // Set core order fields
        order.setUsers(user);
        order.setServiceProvider(sp);
        order.setPickupDate(pickupDate);
        order.setPickupTime(pickupTime);
        order.setContactName(contactName);
        order.setContactPhone(contactPhone);
        order.setContactAddress(contactAddress);
        order.setLatitude(latitude);
        order.setLongitude(longitude);
        order.setStatus(status);

        // Save order FIRST so it has an ID for child entities
        order = orderRepository.save(order);

        // Now safe to parse booking items and schedule plan
        List<OrderItemRequest> itemRequests = parseItemsFromRedis(data);
        List<BookingItem> bookingItems = buildBookingItems(order, sp, itemRequests);
        bookingItemRepository.saveAll(bookingItems);

        order.setBookingItems(bookingItems); // Attach to order

        // Now save schedule plan (if present)
        saveSchedulePlanIfPresent(order, data);

        // Store back orderId in Redis
        redisTemplate.opsForHash().put(redisKey, "orderId", order.getOrderId());

        return order;

    } catch (Exception e) {
        throw new IllegalStateException("Failed to parse order data from Redis", e);
    }
}
    private List<OrderItemRequest> parseItemsFromRedis(Map<Object, Object> data) {
        try {
            Object itemsObj = data.get("items");
            if (itemsObj == null) {
                throw new IllegalStateException("Items data not found in Redis");
            }

            String itemsJson = itemsObj.toString();
            List<OrderItemRequest> items = objectMapper.readValue(itemsJson, new TypeReference<>() {});
            if (items == null || items.isEmpty()) {
                throw new IllegalStateException("Items list is empty or invalid");
            }
            return items;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse items from Redis", e);
        }
    }

    private List<BookingItem> buildBookingItems(Order order, ServiceProvider sp, List<OrderItemRequest> itemRequests) {
        List<BookingItem> bookingItems = new ArrayList<>();
        for (OrderItemRequest itemDto : itemRequests) {
            Integer quantity = itemDto.getQuantity();
            if (itemDto.getItemId() == null || quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Invalid item or quantity in order request");
            }

            Items item = itemsRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemDto.getItemId()));
            Price price = priceRepository.findByServiceProviderAndItem(sp, item)
                    .orElseThrow(() -> new IllegalArgumentException("Price not found for item: " + itemDto.getItemId()));

            double finalPrice = price.getPrice().doubleValue() * quantity;

            bookingItems.add(BookingItem.builder()
                    .order(order)
                    .item(item)
                    .quantity(quantity)
                    .finalPrice(finalPrice)
                    .build());
        }
        return bookingItems;
    }
    private void saveSchedulePlanIfPresent(Order order, Map<Object, Object> data) {
        boolean goWithSchedulePlan = Boolean.parseBoolean(String.valueOf(data.getOrDefault("goWithSchedulePlan", "false")));
        if (!goWithSchedulePlan) return;

        String schedulePlanStr = (String) data.get("schedulePlan");
        if (schedulePlanStr == null) {
            throw new IllegalStateException("SchedulePlan is missing despite goWithSchedulePlan=true");
        }

        boolean payEachDelivery = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payEachDelivery", "false")));
        boolean payLastDelivery = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payLastDelivery", "false")));

        SchedulePlanRequestDto dto = new SchedulePlanRequestDto(
                SchedulePlan.valueOf(schedulePlanStr),
                payEachDelivery,
                payLastDelivery
        );
        dto.validate();

        // Don't save yet, just attach to order
        OrderSchedulePlan plan = OrderSchedulePlan.builder()
                .order(order)  // set order reference
                .schedulePlan(dto.getSchedulePlan())
                .payEachDelivery(dto.isPayEachDelivery())
                .payLastDelivery(dto.isPayLastDelivery())
                .build();

        // Only attach it to order for later persistence
        order.setOrderSchedulePlan(plan);
    }


}
