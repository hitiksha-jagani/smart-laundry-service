//package com.SmartLaundry.service.Customer;
//
//import com.SmartLaundry.dto.Customer.*;
//import com.SmartLaundry.dto.DeliveryAgent.FeedbackAgentRequestDto;
//import com.SmartLaundry.dto.ServiceProvider.OrderMapper;
//import com.SmartLaundry.model.*;
//import com.SmartLaundry.repository.*;
//import com.SmartLaundry.service.ServiceProvider.ServiceProviderOrderService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import com.SmartLaundry.dto.Customer.BookOrderRequestDto;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeParseException;
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class OrderService implements OrderBookingService {
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
//    private final SMSService smsService;
//    private final EmailService emailService;
//    private final RescheduleRepository rescheduleRepository;
//    private final FeedbackProvidersRepository feedbackProvidersRepository;
//    private final TicketRepository ticketRepository;
//    private final FAQRepository faqRepository;
//    private final FeedbackAgentsRepository feedbackAgentsRepository;
//    private final DeliveryAgentRepository deliveryAgentRepository;
//    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
//
//
//    private String getRedisKey(String userId) {
//        return "order:user:" + userId;
//    }
//
//
//    // Save initial order details in Redis
//    public void saveInitialOrderDetails(String userId, BookOrderRequestDto dto) {
//        if (dto == null || dto.getServiceProviderId() == null || dto.getPickupDate() == null ||
//                dto.getPickupTime() == null || dto.getItems() == null || dto.getItems().isEmpty()) {
//            throw new IllegalArgumentException("Missing required fields in BookOrderRequestDto");
//        }
//
//        String key = getRedisKey(userId);
//
//        redisTemplate.opsForHash().put(key, "serviceProviderId", dto.getServiceProviderId());
//        redisTemplate.opsForHash().put(key, "pickupDate", dto.getPickupDate().toString());
//        redisTemplate.opsForHash().put(key, "pickupTime", dto.getPickupTime().toString());
//        redisTemplate.opsForHash().put(key, "goWithSchedulePlan", String.valueOf(dto.isGoWithSchedulePlan()));
//        try {
//            redisTemplate.opsForHash().put(key, "items", objectMapper.writeValueAsString(dto.getItems()));
//        } catch (JsonProcessingException e) {
//            throw new IllegalStateException("Failed to serialize items list", e);
//        }
//        redisTemplate.expire(key, Duration.ofDays(7));
//    }
//
//    // Save schedule plan details in Redis
//    public void saveSchedulePlan(String userId, SchedulePlanRequestDto dto) {
//        if (dto == null || dto.getSchedulePlan() == null) {
//            throw new IllegalArgumentException("Schedule plan or selection is missing");
//        }
//
//        String key = getRedisKey(userId);
//
//        redisTemplate.opsForHash().put(key, "schedulePlan", dto.getSchedulePlan().name());
//        redisTemplate.opsForHash().put(key, "payEachDelivery", String.valueOf(dto.isPayEachDelivery()));
//        redisTemplate.opsForHash().put(key, "payLastDelivery", String.valueOf(dto.isPayLastDelivery()));
//        redisTemplate.expire(key, Duration.ofDays(7));
//    }
//
//    // Save contact info in Redis
//    public void saveContactInfo(String userId, ContactDetailsDto dto) {
//        if (dto == null || dto.getContactName() == null || dto.getContactPhone() == null ||
//                dto.getContactAddress() == null || dto.getLatitude() == null || dto.getLongitude() == null) {
//            throw new IllegalArgumentException("Missing required contact fields");
//        }
//
//        String key = getRedisKey(userId);
//
//        redisTemplate.opsForHash().put(key, "contactName", dto.getContactName());
//        redisTemplate.opsForHash().put(key, "contactPhone", dto.getContactPhone());
//        redisTemplate.opsForHash().put(key, "contactAddress", dto.getContactAddress());
//        redisTemplate.opsForHash().put(key, "latitude", String.valueOf(dto.getLatitude()));
//        redisTemplate.opsForHash().put(key, "longitude", String.valueOf(dto.getLongitude()));
//        redisTemplate.expire(key, Duration.ofDays(7));
//    }
//
//    // Validate that all required fields exist in Redis before order placement
//    public void validateRedisOrderData(String userId) {
//        Map<Object, Object> data = redisTemplate.opsForHash().entries(getRedisKey(userId));
//        if (data.isEmpty())
//            throw new IllegalStateException("No order data found for user: " + userId);
//
//        List<String> requiredFields = List.of(
//                "serviceProviderId", "pickupDate", "pickupTime",
//                "contactName", "contactPhone", "contactAddress",
//                "latitude", "longitude", "items"
//        );
//        for (String field : requiredFields) {
//            if (!data.containsKey(field) || data.get(field) == null) {
//                throw new IllegalStateException("Missing required field '" + field + "' in Redis order data");
//            }
//        }
//    }
//
//    public void placeOrder(String userId) {
//        validateRedisOrderData(userId);
//
//        String key = getRedisKey(userId);
//        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
//        if (data.isEmpty()) throw new IllegalStateException("No order data found to place order");
//
//        // Load user and service provider
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
//
//        String spId = (String) data.get("serviceProviderId");
//        ServiceProvider sp = serviceProviderRepository.findById(spId)
//                .orElseThrow(() -> new IllegalArgumentException("Service provider not found: " + spId));
//
//        // Extract core fields
//        LocalDate pickupDate = LocalDate.parse((String) data.get("pickupDate"));
//        LocalTime pickupTime = LocalTime.parse((String) data.get("pickupTime"));
//        String contactName = (String) data.get("contactName");
//        String contactPhone = (String) data.get("contactPhone");
//        String contactAddress = (String) data.get("contactAddress");
//
//        // Build initial order
//        Order order = Order.builder()
//                .users(user)
//                .serviceProvider(sp)
//                .pickupDate(pickupDate)
//                .pickupTime(pickupTime)
//                .contactName(contactName)
//                .contactPhone(contactPhone)
//                .contactAddress(contactAddress)
//                .status(OrderStatus.PENDING)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        // Attach schedule plan if present
//        saveSchedulePlanIfPresent(order, data);
//
//        // Save order (including plan if set)
//        order = orderRepository.save(order);
//
//        if (order.getOrderSchedulePlan() != null) {
//            order.getOrderSchedulePlan().setOrder(order);
//            orderSchedulePlanRepository.save(order.getOrderSchedulePlan());
//        }
//
//        // Parse items and build booking items
//        List<OrderItemRequest> itemRequests = parseItemsFromRedis(data);
//        List<BookingItem> bookingItems = buildBookingItems(order, sp, itemRequests);
//
//        for (BookingItem item : bookingItems) {
//            bookingItemRepository.save(item);
//        }
//
//        // Add orderId to service provider's pending set
//        redisTemplate.opsForSet().add(ServiceProviderOrderService.getPendingOrdersSetKey(spId), order.getOrderId());
//
//        // Notify service provider
//        String customerName = user.getFirstName();
//        smsService.sendOrderStatusNotification(
//                sp.getUser().getPhoneNo(),
//                "New laundry order received from " + customerName + ". Please check your dashboard."
//        );
//        emailService.sendOrderStatusNotification(
//                sp.getUser().getEmail(),
//                "New Laundry Order Request",
//                "You have received a new laundry order from " + customerName + ". Please accept or reject it."
//        );
//
//        // Optionally mark Redis status as "PENDING" and expire in 7 days (for fallback/tracking)
//        redisTemplate.opsForHash().put(key, "status", "PENDING");
//        redisTemplate.expire(key, Duration.ofDays(7));
//
//        // Final cleanup (remove Redis data if no longer needed)
//        redisTemplate.delete(key);
//    }
//
//
//    // Build order response DTO from Redis stored data
//    public  OrderResponseDto buildOrderResponseDtoFromRedisData(String userId, Map<Object, Object> data) {
//        if (data == null || data.isEmpty()) {
//            throw new IllegalArgumentException("No Redis data found for user: " + userId);
//        }
//
//        OrderResponseDto.OrderResponseDtoBuilder dtoBuilder = OrderResponseDto.builder();
//
//        dtoBuilder.orderId("PENDING:" + userId); // temp id
//        dtoBuilder.userId(userId);
//        dtoBuilder.serviceProviderId((String) data.get("serviceProviderId"));
//
//        // Pickup date/time parsing
//        String pickupDateStr = (String) data.get("pickupDate");
//        if (pickupDateStr != null) {
//            dtoBuilder.pickupDate(LocalDate.parse(pickupDateStr));
//        }
//        String pickupTimeStr = (String) data.get("pickupTime");
//        if (pickupTimeStr != null) {
//            dtoBuilder.pickupTime(LocalTime.parse(pickupTimeStr));
//        }
//
//        // Contact info
//        dtoBuilder.contactName((String) data.get("contactName"));
//        dtoBuilder.contactPhone((String) data.get("contactPhone"));
//        dtoBuilder.contactAddress((String) data.get("contactAddress"));
//
//        // Latitude and Longitude parsing
//        String latStr = (String) data.get("latitude");
//        if (latStr != null) {
//            dtoBuilder.latitude(Double.parseDouble(latStr));
//        }
//        String lonStr = (String) data.get("longitude");
//        if (lonStr != null) {
//            dtoBuilder.longitude(Double.parseDouble(lonStr));
//        }
//
//        dtoBuilder.status(OrderStatus.PENDING);
//
//        // Parse booking items JSON into BookingItemDto list
//        String itemsJson = (String) data.get("items");
//        List<OrderResponseDto.BookingItemDto> bookingItems = new ArrayList<>();
//        if (itemsJson != null) {
//            try {
//                List<OrderItemRequest> orderItems = objectMapper.readValue(itemsJson, new TypeReference<List<OrderItemRequest>>() {});
//                for (OrderItemRequest itemReq : orderItems) {
//                    String itemId = itemReq.getItemId();
//                    Integer quantity = itemReq.getQuantity();
//
//                    Items item = itemsRepository.findById(itemId).orElse(null);
//                    Price price = null;
//                    if (item != null) {
//                        ServiceProvider sp = serviceProviderRepository.findById((String) data.get("serviceProviderId")).orElse(null);
//                        if (sp != null) {
//                            price = priceRepository.findByServiceProviderAndItem(sp, item).orElse(null);
//                        }
//                    }
//
//                    Double finalPrice = (price != null && quantity != null) ? price.getPrice().doubleValue() * quantity : null;
//
//                    bookingItems.add(OrderResponseDto.BookingItemDto.builder()
//                            .itemId(itemId)
//                            .itemName(item != null ? item.getItemName() : null)
//                            .quantity(quantity)
//                            .finalPrice(finalPrice)
//                            .build());
//                }
//            } catch (Exception e) {
//                log.error("Error parsing items JSON for user " + userId, e);
//            }
//        }
//        dtoBuilder.bookingItems(bookingItems);
//
//        // Schedule plan if present
//        String planStr = (String) data.get("schedulePlan");
//        if (planStr != null) {
//            boolean payEach = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payEachDelivery", "false")));
//            boolean payLast = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payLastDelivery", "false")));
//            dtoBuilder.schedulePlan(OrderResponseDto.SchedulePlanDto.builder()
//                    .plan(planStr)
//                    .payEachDelivery(payEach)
//                    .payLastDelivery(payLast)
//                    .build());
//        } else {
//            dtoBuilder.schedulePlan(null);
//        }
//
//        return dtoBuilder.build();
//    }
//
//    // Finalize and create actual Order entity from Redis and save to DB
//    public OrderResponseDto createOrder(String userId) {
//        validateRedisOrderData(userId);
//
//        // Build Order (without saving)
//        Order order = buildOrderFromRedis(userId, OrderStatus.PENDING);
//
//        // Save Order first (to generate orderId)
//        order = orderRepository.save(order);
//
//        // Save schedule plan if present
//        if (order.getOrderSchedulePlan() != null) {
//            orderSchedulePlanRepository.save(order.getOrderSchedulePlan());
//        }
//
//        // Save Booking Items
//        if (order.getBookingItems() != null && !order.getBookingItems().isEmpty()) {
//            bookingItemRepository.saveAll(order.getBookingItems());
//        }
//
//        // Clean Redis
//        redisTemplate.delete(getRedisKey(userId));
//        redisTemplate.opsForSet().remove(
//                ServiceProviderOrderService.getPendingOrdersSetKey(order.getServiceProvider().getServiceProviderId()),
//                userId
//        );
//
//        // Send notification
//        ServiceProvider sp = order.getServiceProvider();
//        smsService.sendOrderStatusNotification(
//                sp.getUser().getPhoneNo(),
//                "New laundry order received from user " + order.getUsers().getFirstName()
//        );
//        emailService.sendOrderStatusNotification(
//                sp.getUser().getEmail(),
//                "New Laundry Order Request",
//                "You have received a new laundry order from " + order.getUsers().getFirstName()
//        );
//
//        // Return OrderResponseDto
//        return orderMapper.toOrderResponseDto(order);
//    }
//
//    @Transactional
//    public Order buildOrderFromRedis(String userId, OrderStatus status) {
//        String redisKey = getRedisKey(userId);
//        Map<Object, Object> data = redisTemplate.opsForHash().entries(redisKey);
//        if (data.isEmpty()) {
//            throw new IllegalStateException("No order data found for user: " + userId);
//        }
//
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
//
//        String spId = (String) data.get("serviceProviderId");
//        if (spId == null || spId.isBlank()) {
//            throw new IllegalStateException("Service Provider ID missing in Redis");
//        }
//        ServiceProvider sp = serviceProviderRepository.findById(spId)
//                .orElseThrow(() -> new IllegalArgumentException("Service provider not found: " + spId));
//
//        LocalDate pickupDate = LocalDate.parse((String) data.get("pickupDate"));
//        LocalTime pickupTime = LocalTime.parse((String) data.get("pickupTime"));
//        String contactName = (String) data.get("contactName");
//        String contactPhone = (String) data.get("contactPhone");
//        String contactAddress = (String) data.get("contactAddress");
//
//        double latitude = 0.0;
//        double longitude = 0.0;
//        try {
//            String latStr = (String) data.get("latitude");
//            String lonStr = (String) data.get("longitude");
//            if (latStr != null) latitude = Double.parseDouble(latStr);
//            if (lonStr != null) longitude = Double.parseDouble(lonStr);
//        } catch (NumberFormatException ignored) {}
//
//        Order order = Order.builder()
//                .users(user)
//                .serviceProvider(sp)
//                .pickupDate(pickupDate)
//                .pickupTime(pickupTime)
//                .contactName(contactName)
//                .contactPhone(contactPhone)
//                .contactAddress(contactAddress)
//                .latitude(latitude)
//                .longitude(longitude)
//                .status(status)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        // Attach BookingItems
//        List<OrderItemRequest> itemRequests = parseItemsFromRedis(data);
//        List<BookingItem> bookingItems = buildBookingItems(order, sp, itemRequests);
//        order.setBookingItems(bookingItems);
//
//        // Attach SchedulePlan if present
//        saveSchedulePlanIfPresent(order, data);
//
//        return order;
//    }
//
//    private List<OrderItemRequest> parseItemsFromRedis(Map<Object, Object> data) {
//        try {
//            Object itemsObj = data.get("items");
//            if (itemsObj == null) {
//                throw new IllegalStateException("Items data not found in Redis");
//            }
//
//            String itemsJson = itemsObj.toString();
//            List<OrderItemRequest> items = objectMapper.readValue(itemsJson, new TypeReference<>() {});
//            if (items == null || items.isEmpty()) {
//                throw new IllegalStateException("Items list is empty or invalid");
//            }
//            return items;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to parse items from Redis", e);
//        }
//    }
//
//    private List<BookingItem> buildBookingItems(Order order, ServiceProvider sp, List<OrderItemRequest> itemRequests) {
//        List<BookingItem> bookingItems = new ArrayList<>();
//        for (OrderItemRequest itemDto : itemRequests) {
//            Integer quantity = itemDto.getQuantity();
//            if (itemDto.getItemId() == null || quantity == null || quantity <= 0) {
//                throw new IllegalArgumentException("Invalid item or quantity in order request");
//            }
//
//            Items item = itemsRepository.findById(itemDto.getItemId())
//                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemDto.getItemId()));
//            Price price = priceRepository.findByServiceProviderAndItem(sp, item)
//                    .orElseThrow(() -> new IllegalArgumentException("Price not found for item: " + itemDto.getItemId()));
//
//            double finalPrice = price.getPrice().doubleValue() * quantity;
//
//            bookingItems.add(BookingItem.builder()
//                    .order(order)
//                    .item(item)
//                    .quantity(quantity)
//                    .finalPrice(finalPrice)
//                    .build());
//        }
//        return bookingItems;
//    }
//    private void saveSchedulePlanIfPresent(Order order, Map<Object, Object> data) {
//        boolean goWithSchedulePlan = Boolean.parseBoolean(String.valueOf(data.getOrDefault("goWithSchedulePlan", "false")));
//        if (!goWithSchedulePlan) return;
//
//        String schedulePlanStr = (String) data.get("schedulePlan");
//        if (schedulePlanStr == null || schedulePlanStr.isBlank()) {
//            throw new IllegalStateException("SchedulePlan is missing despite goWithSchedulePlan=true");
//        }
//
//        SchedulePlan planEnum;
//        try {
//            planEnum = SchedulePlan.valueOf(schedulePlanStr);
//        } catch (IllegalArgumentException e) {
//            throw new IllegalStateException("Invalid schedule plan value: " + schedulePlanStr);
//        }
//
//        boolean payEachDelivery = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payEachDelivery", "false")));
//        boolean payLastDelivery = Boolean.parseBoolean(String.valueOf(data.getOrDefault("payLastDelivery", "false")));
//
//        SchedulePlanRequestDto dto = new SchedulePlanRequestDto(planEnum, payEachDelivery, payLastDelivery);
//        dto.validate();
//
//        OrderSchedulePlan plan = OrderSchedulePlan.builder()
//                .order(order)
//                .schedulePlan(planEnum)
//                .payEachDelivery(payEachDelivery)
//                .payLastDelivery(payLastDelivery)
//                .build();
//
//        order.setOrderSchedulePlan(plan);
//    }
//
//    @Transactional
//    public void cancelOrder(String userId, String orderId) {
//        // 1. Fetch the order by orderId and userId to verify ownership and existence
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
//
//        // Optionally verify the order belongs to this user
//        if (!order.getUsers().getUserId().equals(userId)) {
//            throw new SecurityException("User is not authorized to cancel this order");
//        }
//
//        // 2. Check if order is already cancelled or completed
//        if (order.getStatus() == OrderStatus.CANCELLED) {
//            throw new IllegalStateException("Order is already cancelled");
//        }
//        if (order.getStatus() == OrderStatus.COMPLETED) {
//            throw new IllegalStateException("Completed orders cannot be cancelled");
//        }
//
//        // 3. Update order status to CANCELLED
//        order.setStatus(OrderStatus.CANCELLED);
//        orderRepository.save(order);
//
//        // 4. Notify service provider
//        ServiceProvider sp = order.getServiceProvider();
//        String message = "Order " + order.getOrderId() + " from user " + userId + " has been cancelled.";
//
//        smsService.sendOrderStatusNotification(sp.getUser().getPhoneNo(), message);
//        emailService.sendOrderStatusNotification(
//                sp.getUser().getEmail(),
//                "Order Cancelled Notification",
//                message
//        );
//    }
//
//    public void rescheduleOrder(String userId, String orderId, RescheduleRequestDto dto) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (!order.getUsers().getUserId().equals(userId)) {
//            throw new RuntimeException("Unauthorized to reschedule this order");
//        }
//
//        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
//            throw new RuntimeException("Order cannot be rescheduled. It is already " + order.getStatus());
//        }
//
//        LocalTime parsedSlot;
//        try {
//            parsedSlot = LocalTime.parse(dto.getSlot());
//        } catch (DateTimeParseException e) {
//            throw new IllegalArgumentException("Invalid time slot format. Expected format: HH:mm");
//        }
//
//        Reschedule reschedule = Reschedule.builder()
//                .order(order)
//                .date(dto.getDate())
//                .slot(dto.getSlot())
//                .build();
//        rescheduleRepository.save(reschedule);
//
//        order.setPickupDate(dto.getDate());
//        order.setPickupTime(parsedSlot);
//        order.setStatus(OrderStatus.RESCHEDULED);
//        orderRepository.save(order);
//
//        ServiceProvider sp = order.getServiceProvider();
//        String message = String.format("Order %s from user %s has been rescheduled to %s at %s.",
//                order.getOrderId(), userId, dto.getDate(), dto.getSlot());
//
//        smsService.sendOrderStatusNotification(sp.getUser().getPhoneNo(), message);
//        emailService.sendOrderStatusNotification(
//                sp.getUser().getEmail(),
//                "Order Rescheduled Notification",
//                message
//        );
//
//        log.info("Order {} rescheduled by user {} to {} at {}", order.getOrderId(), userId, dto.getDate(), dto.getSlot());
//    }
//
//
//
//    public TrackOrderResponseDto trackOrder(String userId, String orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (!order.getUsers().getUserId().equals(userId)) {
//            throw new RuntimeException("You are not authorized to track this order");
//        }
//
//        TrackOrderResponseDto dto = new TrackOrderResponseDto();
//        dto.setOrderId(order.getOrderId());
//        dto.setStatus(order.getStatus().name());
//        dto.setPickupDate(order.getPickupDate());
//        dto.setPickupTime(order.getPickupTime());
//
//        return dto;
//    }
//
//    public void submitFeedbackProviders(String userId, FeedbackRequestDto dto) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        ServiceProvider provider = serviceProviderRepository.findById(dto.getServiceProviderId())
//                .orElseThrow(() -> new RuntimeException("Service Provider not found"));
//
//        FeedbackProviders feedback = new FeedbackProviders();
//        feedback.setUser(user);
//        feedback.setServiceProvider(provider);
//        feedback.setRating(dto.getRating());
//        feedback.setReview(dto.getReview());
//
//        feedbackProvidersRepository.save(feedback);
//    }
//    //for Delivery Agent
//    public void submitFeedbackAgents(String userId, FeedbackAgentRequestDto dto) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        DeliveryAgent agent = deliveryAgentRepository.findById(dto.getAgentId())
//                .orElseThrow(() -> new RuntimeException("Delivery Agent not found"));
//
//        FeedbackAgents feedback = new FeedbackAgents();
//        feedback.setUser(user);
//        feedback.setAgent(agent);
//        feedback.setRating(dto.getRating());
//        feedback.setReview(dto.getReview());
//
//        feedbackAgentsRepository.save(feedback);
//    }
//
//
//    public void raiseTicket(String userId, RaiseTicketRequestDto dto) {
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Ticket ticket = Ticket.builder()
//                .user(user)
//                .title(dto.getTitle())
//                .description(dto.getDescription())
//                .photo(dto.getPhoto())
//                .category(dto.getCategory())
//                .status("OPEN")
//                .submittedAt(LocalDateTime.now())
//                .build();
//
//        ticketRepository.save(ticket);
//    }
//
//}
