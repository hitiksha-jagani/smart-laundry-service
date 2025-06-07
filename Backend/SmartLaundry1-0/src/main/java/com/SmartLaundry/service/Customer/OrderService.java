//package com.SmartLaundry.service.Customer;
//
//import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.SmartLaundry.dto.Customer.*;
//import com.SmartLaundry.model.*;
//import com.SmartLaundry.repository.*;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class OrderService implements OrderBookingService{
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper;
//    private final OrderRepository orderRepository;
//    private final ServiceProviderRepository serviceProviderRepository;
//    private final ItemsRepository itemsRepository;
//    private final PriceRepository priceRepository;
//    private final BookingItemRepository bookingItemRepository;
//    private final OrderSchedulePlanRepository orderSchedulePlanRepository;
//    private final UserRepository userRepository;
//    private final OrderMapper orderMapper;
//
//    private String getRedisKey(String userId) {
//        return "order:user:" + userId;
//    }
//
//    public void saveInitialOrderDetails(String userId, BookOrderRequestDto dto) {
//        String key = getRedisKey(userId);
//        redisTemplate.opsForHash().put(key, "serviceProviderId", dto.getServiceProviderId());
//        redisTemplate.opsForHash().put(key, "pickupDate", dto.getPickupDate().toString());
//        redisTemplate.opsForHash().put(key, "pickupTime", dto.getPickupTime().toString());
//        redisTemplate.opsForHash().put(key, "goWithSchedulePlan", dto.isGoWithSchedulePlan());
//
//        try {
//            String itemsJson = objectMapper.writeValueAsString(dto.getItems());
//            redisTemplate.opsForHash().put(key, "items", itemsJson);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to store items in Redis", e);
//        }
//    }
//
//    public void saveSchedulePlan(String userId, SchedulePlanRequestDto dto) {
//        String key = getRedisKey(userId);
//        redisTemplate.opsForHash().put(key, "schedulePlan", dto.getSchedulePlan().name());
//        redisTemplate.opsForHash().put(key, "payEachDelivery", dto.isPayEachDelivery());
//        redisTemplate.opsForHash().put(key, "payLastDelivery", dto.isPayLastDelivery());
//    }
//
//    public void saveContactInfo(String userId, ContactDetailsDto dto) {
//        String key = getRedisKey(userId);
//        redisTemplate.opsForHash().put(key, "contactName", dto.getContactName());
//        redisTemplate.opsForHash().put(key, "contactPhone", dto.getContactPhone());
//        redisTemplate.opsForHash().put(key, "contactAddress", dto.getContactAddress());
//        redisTemplate.opsForHash().put(key, "latitude", dto.getLatitude());
//        redisTemplate.opsForHash().put(key, "longitude", dto.getLongitude());
//    }
//
////    public Order createOrder(String userId) {
////        String key = getRedisKey(userId);
////        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
////
////        if (data.isEmpty()) {
////            throw new IllegalStateException("No order data found for user: " + userId);
////        }
////
////        Users user = userRepository.findById(userId)
////                .orElseThrow(() -> new IllegalArgumentException("User not found"));
////        ServiceProvider sp = serviceProviderRepository.findById((String) data.get("serviceProviderId"))
////                .orElseThrow(() -> new IllegalArgumentException("Service provider not found"));
////
////        LocalDate pickupDate = LocalDate.parse((String) data.get("pickupDate"));
////        LocalTime pickupTime = LocalTime.parse((String) data.get("pickupTime"));
////
////        Order order = Order.builder()
////                .users(user)
////                .serviceProvider(sp)
////                .pickupDate(pickupDate)
////                .pickupTime(pickupTime)
////                .contactName((String) data.get("contactName"))
////                .contactPhone((String) data.get("contactPhone"))
////                .contactAddress((String) data.get("contactAddress"))
////                .latitude(Double.parseDouble(data.get("latitude").toString()))
////                .longitude(Double.parseDouble(data.get("longitude").toString()))
////                .status(OrderStatus.PENDING)
////                .build();
////
////        order = orderRepository.save(order);
////
////        // Parse items and create BookingItems
////        List<OrderItemRequest> itemRequests;
////        try {
////            itemRequests = objectMapper.readValue((String) data.get("items"),
////                    new TypeReference<List<OrderItemRequest>>() {});
////        } catch (JsonProcessingException e) {
////            throw new RuntimeException("Failed to parse booking items from Redis", e);
////        }
////
////        List<BookingItem> bookingItems = new ArrayList<>();
////        for (OrderItemRequest itemDto : itemRequests) {
////            Items item = itemsRepository.findById(itemDto.getItemId())
////                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemDto.getItemId()));
////            Price price = priceRepository.findByServiceProviderAndItem(sp, item)
////                    .orElseThrow(() -> new IllegalArgumentException("Price not found for item " + itemDto.getItemId()));
////            double finalPrice = itemDto.getQuantity() * price.getPrice();
////
////            BookingItem bookingItem = BookingItem.builder()
////                    .order(order)
////                    .item(item)
////                    .quantity(itemDto.getQuantity())
////                    .finalPrice(finalPrice)
////                    .build();
////
////            bookingItems.add(bookingItem);
////        }
////
////        bookingItemRepository.saveAll(bookingItems);
////
////        // Save schedule plan if applicable
////        if (Boolean.parseBoolean(data.get("goWithSchedulePlan").toString())) {
////            OrderSchedulePlan plan = OrderSchedulePlan.builder()
////                    .order(order)
////                    .schedulePlan(SchedulePlan.valueOf(data.get("schedulePlan").toString()))
////                    .payEachDelivery(Boolean.parseBoolean(data.get("payEachDelivery").toString()))
////                    .payLastDelivery(Boolean.parseBoolean(data.get("payLastDelivery").toString()))
////                    .build();
////
////            orderSchedulePlanRepository.save(plan);
////        }
////
////        // Clean up Redis cache after successful order creation
////        redisTemplate.delete(key);
////
////        return order;
////    }
//
//    public OrderResponseDto createOrder(String userId) {
//        Order order = buildOrderFromRedis(userId, OrderStatus.PENDING);
//        return orderMapper.toOrderResponseDto(order);
//    }
//
//
//
//
//    //service provider Accept/Reject order
//    public Order acceptOrder(String userId) {
//        String key = getRedisKey(userId);
//        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
//
//        if (data.isEmpty()) {
//            throw new IllegalStateException("No order data found in Redis for user: " + userId);
//        }
//        if (!"true".equals(data.get("confirmed"))) {
//            throw new IllegalStateException("Order not confirmed yet");
//        }
//
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        ServiceProvider sp = serviceProviderRepository.findById((String) data.get("serviceProviderId"))
//                .orElseThrow(() -> new IllegalArgumentException("Service provider not found"));
//
//        LocalDate pickupDate = LocalDate.parse((String) data.get("pickupDate"));
//        LocalTime pickupTime = LocalTime.parse((String) data.get("pickupTime"));
//
//        Order order = Order.builder()
//                .users(user)
//                .serviceProvider(sp)
//                .pickupDate(pickupDate)
//                .pickupTime(pickupTime)
//                .contactName((String) data.get("contactName"))
//                .contactPhone((String) data.get("contactPhone"))
//                .contactAddress((String) data.get("contactAddress"))
//                .latitude(Double.parseDouble(data.get("latitude").toString()))
//                .longitude(Double.parseDouble(data.get("longitude").toString()))
//                .status(OrderStatus.ACCEPTED)
//                .build();
//
//        order = orderRepository.save(order);
//
//        List<OrderItemRequest> itemRequests;
//        try {
//            itemRequests = objectMapper.readValue((String) data.get("items"),
//                    new TypeReference<List<OrderItemRequest>>() {});
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to parse booking items from Redis", e);
//        }
//
//        List<BookingItem> bookingItems = new ArrayList<>();
//        for (OrderItemRequest itemDto : itemRequests) {
//            Items item = itemsRepository.findById(itemDto.getItemId())
//                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemDto.getItemId()));
//            Price price = priceRepository.findByServiceProviderAndItem(sp, item)
//                    .orElseThrow(() -> new IllegalArgumentException("Price not found for item " + itemDto.getItemId()));
//            double finalPrice = itemDto.getQuantity() * price.getPrice();
//
//            BookingItem bookingItem = BookingItem.builder()
//                    .order(order)
//                    .item(item)
//                    .quantity(itemDto.getQuantity())
//                    .finalPrice(finalPrice)
//                    .build();
//
//            bookingItems.add(bookingItem);
//        }
//        bookingItemRepository.saveAll(bookingItems);
//
//        if (Boolean.parseBoolean(data.get("goWithSchedulePlan").toString())) {
//            OrderSchedulePlan plan = OrderSchedulePlan.builder()
//                    .order(order)
//                    .schedulePlan(SchedulePlan.valueOf(data.get("schedulePlan").toString()))
//                    .payEachDelivery(Boolean.parseBoolean(data.get("payEachDelivery").toString()))
//                    .payLastDelivery(Boolean.parseBoolean(data.get("payLastDelivery").toString()))
//                    .build();
//
//            orderSchedulePlanRepository.save(plan);
//        }
//
//        redisTemplate.delete(key);
//
//        return order;
//    }
//    public void rejectOrder(String userId) {
//        String key = getRedisKey(userId);
//        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
//
//        if (data.isEmpty()) {
//            throw new IllegalStateException("No order data found in Redis for user: " + userId);
//        }
//
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        ServiceProvider sp = serviceProviderRepository.findById((String) data.get("serviceProviderId"))
//                .orElseThrow(() -> new IllegalArgumentException("Service provider not found"));
//
//        LocalDate pickupDate = LocalDate.parse((String) data.get("pickupDate"));
//        LocalTime pickupTime = LocalTime.parse((String) data.get("pickupTime"));
//
//        Order rejectedOrder = Order.builder()
//                .users(user)
//                .serviceProvider(sp)
//                .pickupDate(pickupDate)
//                .pickupTime(pickupTime)
//                .contactName((String) data.get("contactName"))
//                .contactPhone((String) data.get("contactPhone"))
//                .contactAddress((String) data.get("contactAddress"))
//                .latitude(Double.parseDouble(data.get("latitude").toString()))
//                .longitude(Double.parseDouble(data.get("longitude").toString()))
//                .status(OrderStatus.REJECTED)
//                .build();
//
//        orderRepository.save(rejectedOrder);
//
//        redisTemplate.delete(key); // remove from Redis
//    }
//
//
//}


package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.*;
import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
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

    private String getRedisKey(String userId) {
        return "order:user:" + userId;
    }

    public void saveInitialOrderDetails(String userId, BookOrderRequestDto dto) {
        String key = getRedisKey(userId);
        redisTemplate.opsForHash().put(key, "serviceProviderId", dto.getServiceProviderId());
        redisTemplate.opsForHash().put(key, "pickupDate", dto.getPickupDate().toString());
        redisTemplate.opsForHash().put(key, "pickupTime", dto.getPickupTime().toString());
        redisTemplate.opsForHash().put(key, "goWithSchedulePlan", String.valueOf(dto.isGoWithSchedulePlan()));
        try {
            redisTemplate.opsForHash().put(key, "items", objectMapper.writeValueAsString(dto.getItems()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to store items in Redis", e);
        }
        // Set expiration every time data is saved to keep it alive
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public void saveSchedulePlan(String userId, SchedulePlanRequestDto dto) {
        String key = getRedisKey(userId);
        redisTemplate.opsForHash().put(key, "schedulePlan", dto.getSchedulePlan().name());
        redisTemplate.opsForHash().put(key, "payEachDelivery", String.valueOf(dto.isPayEachDelivery()));
        redisTemplate.opsForHash().put(key, "payLastDelivery", String.valueOf(dto.isPayLastDelivery()));
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public void saveContactInfo(String userId, ContactDetailsDto dto) {
        String key = getRedisKey(userId);
        redisTemplate.opsForHash().put(key, "contactName", dto.getContactName());
        redisTemplate.opsForHash().put(key, "contactPhone", dto.getContactPhone());
        redisTemplate.opsForHash().put(key, "contactAddress", dto.getContactAddress());
        redisTemplate.opsForHash().put(key, "latitude", String.valueOf(dto.getLatitude()));
        redisTemplate.opsForHash().put(key, "longitude", String.valueOf(dto.getLongitude()));
        redisTemplate.expire(key, Duration.ofDays(7));
    }

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

    public OrderResponseDto createOrder(String userId) {
        validateRedisOrderData(userId); // Optional: validate before building
        Order order = buildOrderFromRedis(userId, OrderStatus.PENDING);
        redisTemplate.delete(getRedisKey(userId));
        return orderMapper.toOrderResponseDto(order);
    }

    public OrderResponseDto acceptOrder(String userId) {
        Map<Object, Object> data = redisTemplate.opsForHash().entries(getRedisKey(userId));
        Object confirmedObj = data.get("confirmed");
        if (confirmedObj == null || !"true".equalsIgnoreCase(confirmedObj.toString())) {
            throw new IllegalStateException("Order not confirmed yet");
        }
        Order order = buildOrderFromRedis(userId, OrderStatus.ACCEPTED);
        redisTemplate.delete(getRedisKey(userId));

        String customerPhone = order.getContactPhone();
        String email = order.getUsers().getEmail();
        String message = "Your LaundryService Order " + order.getOrderId() + " is Accepted";

        smsService.sendOrderStatusNotification(customerPhone, message);
        emailService.sendOrderStatusNotification(email, "Order Accepted", message);

        return orderMapper.toOrderResponseDto(order);
    }

    public void rejectOrder(String userId) {
        Order order = buildOrderFromRedis(userId, OrderStatus.REJECTED);
        redisTemplate.delete(getRedisKey(userId));
        String customerPhone = order.getContactPhone();
        String email = order.getUsers().getEmail();

        String message = "We’re sorry! Your order " + order.getOrderId() + " has been rejected.";

        smsService.sendOrderStatusNotification(customerPhone, message);
        emailService.sendOrderStatusNotification(email, "Order Rejected", message);
    }

    // ------------------------ PRIVATE HELPERS ------------------------

    private Order buildOrderFromRedis(String userId, OrderStatus status) {
        Map<Object, Object> data = redisTemplate.opsForHash().entries(getRedisKey(userId));
        if (data.isEmpty())
            throw new IllegalStateException("No order data found for user: " + userId);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        String spId = (String) data.get("serviceProviderId");
        ServiceProvider sp = serviceProviderRepository.findById(spId)
                .orElseThrow(() -> new IllegalArgumentException("Service provider not found: " + spId));

        Object latObj = data.get("latitude");
        Object longObj = data.get("longitude");
        if (latObj == null || longObj == null) {
            throw new IllegalStateException("Latitude or Longitude is missing from Redis order data");
        }

        Order order = Order.builder()
                .users(user)
                .serviceProvider(sp)
                .pickupDate(LocalDate.parse((String) data.get("pickupDate")))
                .pickupTime(LocalTime.parse((String) data.get("pickupTime")))
                .contactName((String) data.get("contactName"))
                .contactPhone((String) data.get("contactPhone"))
                .contactAddress((String) data.get("contactAddress"))
                .latitude(Double.parseDouble(latObj.toString()))
                .longitude(Double.parseDouble(longObj.toString()))
                .status(status)
                .build();

        order = orderRepository.save(order);

        List<OrderItemRequest> itemRequests = parseItemsFromRedis(data);
        List<BookingItem> bookingItems = buildBookingItems(order, sp, itemRequests);
        bookingItemRepository.saveAll(bookingItems);

        saveSchedulePlanIfPresent(order, data);

        return order;
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
            Items item = itemsRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemDto.getItemId()));
            Price price = priceRepository.findByServiceProviderAndItem(sp, item)
                    .orElseThrow(() -> new IllegalArgumentException("Price not found for item: " + itemDto.getItemId()));

            bookingItems.add(BookingItem.builder()
                    .order(order)
                    .item(item)
                    .quantity(itemDto.getQuantity())
                    .finalPrice(price.getPrice().doubleValue() * itemDto.getQuantity())
                    .build());
        }
        return bookingItems;
    }

    private void saveSchedulePlanIfPresent(Order order, Map<Object, Object> data) {
        boolean goWithSchedulePlan = Boolean.parseBoolean(String.valueOf(data.getOrDefault("goWithSchedulePlan", "false")));
        if (!goWithSchedulePlan) return;

        // Defensive check for schedulePlan presence
        String schedulePlanStr = (String) data.get("schedulePlan");
        if (schedulePlanStr == null) {
            throw new IllegalStateException("SchedulePlan is missing despite goWithSchedulePlan=true");
        }

        OrderSchedulePlan plan = OrderSchedulePlan.builder()
                .order(order)
                .schedulePlan(SchedulePlan.valueOf(schedulePlanStr))
                .payEachDelivery(Boolean.parseBoolean(String.valueOf(data.getOrDefault("payEachDelivery", "false"))))
                .payLastDelivery(Boolean.parseBoolean(String.valueOf(data.getOrDefault("payLastDelivery", "false"))))
                .build();

        orderSchedulePlanRepository.save(plan);
    }
}
