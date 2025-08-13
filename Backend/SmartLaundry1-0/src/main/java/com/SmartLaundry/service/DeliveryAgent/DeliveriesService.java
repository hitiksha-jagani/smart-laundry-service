package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliverySummaryResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderAssignmentDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PendingDeliveriesResponseDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.SMSService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisCommandInterruptedException;
import jakarta.mail.Address;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Math.round;

// @author Hitiksha Jagani
@Service
public class DeliveriesService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private DeliveriesRepository deliveriesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingItemRepository bookingItemRepository;

    @Autowired
    private DeliveryAgentAvailabilityRepository availabilityRepository;

    @Autowired
    private DeliveryAgentEarningsRepository deliveryAgentEarningsRepository;

    @Autowired
    private SMSService smsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(DeliveriesService.class);

    public DeliverySummaryResponseDTO deliveriesSummary(String agentId) {
        Users user = userRepository.findById(agentId).orElseThrow();
        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElseThrow();

        DeliverySummaryResponseDTO deliverySummaryResponseDTO = new DeliverySummaryResponseDTO();
//        deliverySummaryResponseDTO.setTotalDeliveries(deliveriesRepository.countDeliveriesByAgent(deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setPendingDeliveries(countPendingAssignmentsForAgent(deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setTodayDeliveries(
                deliveriesRepository.countAcceptedDeliveryOrdersForToday(LocalDate.now(), deliveryAgent.getDeliveryAgentId()) +
                        deliveriesRepository.countAcceptedPickupOrdersForToday(LocalDate.now(), deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setTotalDeliveries(deliverySummaryResponseDTO.getPendingDeliveries() + deliverySummaryResponseDTO.getTodayDeliveries());

        return deliverySummaryResponseDTO;
    }

    // Count pending orders
    public int countPendingAssignmentsForAgent(String agentId) {
        int count = 0;
        Set<String> keys = redisTemplate.keys("assignment:*");

        if (keys == null || keys.isEmpty()) return 0;

        for (String key : keys) {
            try {
                Object raw = redisTemplate.opsForValue().get(key);
                if (raw == null) continue;


                OrderAssignmentDTO dto = objectMapper.convertValue(raw, OrderAssignmentDTO.class);

                if (dto.getAgentId().equals(agentId) && dto.getStatus() == OrderStatus.ACCEPTED_BY_PROVIDER ||
                        dto.getAgentId().equals(agentId) && dto.getStatus() == OrderStatus.READY_FOR_DELIVERY) {
                    count++;
                }

            } catch (Exception e) {
                System.err.println("Failed to parse value for key: " + key);
                e.printStackTrace();
            }
        }

        return count;
    }

    // Return list of pending deliveries
    @Transactional
    public List<PendingDeliveriesResponseDTO> pendingDeliveries(Users user) throws JsonProcessingException {

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));

        Set<String> assignmentKeys = redisTemplate.keys("assignment:*");
        if (assignmentKeys == null || assignmentKeys.isEmpty()) {
            System.out.println("*****Assignment key is null.");
            return Collections.emptyList(); }

        List<Order> orders = new ArrayList<>();

        for (String key : assignmentKeys) {

            String[] parts = key.split(":");
            if (parts.length < 2) continue;

            String orderId = parts[1];

            System.out.println("order ID : " + orderId);

            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) continue;

            OrderAssignmentDTO assignment;
            try {
                if (value instanceof String json) {
                    assignment = objectMapper.readValue(json, OrderAssignmentDTO.class);
                    System.out.println("Assignment val : " + assignment);
                    System.out.println("value : " + assignment.getAgentId());
                } else {
                    assignment = objectMapper.convertValue(value, OrderAssignmentDTO.class);
                    System.out.println("value : " + assignment.getAgentId());
                    System.out.println("Assignment val : " + assignment);
                }
            } catch (Exception e) {
                continue;
            }

            System.out.println("user id : " + deliveryAgent.getUsers().getUserId());

            System.out.println("Comparing Redis agentId=" + assignment.getAgentId() +
                    " with userId=" + deliveryAgent.getUsers().getUserId());

            if (assignment != null && !assignment.getAgentId().equals(deliveryAgent.getUsers().getUserId())){
                System.out.println("Assignment is not matched.");
                continue;
            }

//            Optional<Order> optionalOrder = orderRepository.findById(orderId);
//            System.out.println("Order ID : " + optionalOrder.get().getOrderId());
//            optionalOrder.ifPresent(orders::add);
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order o = optionalOrder.get();
                System.out.println("Order ID : " + o.getOrderId());
                orders.add(o);
            } else {
                System.out.println("Order ID not found in DB: " + orderId);
            }

        }

        List<PendingDeliveriesResponseDTO> pendingDeliveriesResponseDTOList = new ArrayList<>();

        Double agentLat, agentLon, providerLat, providerLon, customerLat, customerLon, earning;

        DeliveryAgentEarnings deliveryAgentEarnings = deliveryAgentEarningsRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
        if (deliveryAgentEarnings == null) {
            throw new IllegalStateException("No active earnings settings found.");
        }

        for(Order order : orders){
            providerLat = order.getServiceProvider().getUser().getAddress().getLatitude();
            providerLon = order.getServiceProvider().getUser().getAddress().getLongitude();
            customerLat = order.getLatitude();
            customerLon = order.getLongitude();

            Double totalKm;

            System.out.println("****DB Order Status: " + order.getStatus());

            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)){
                System.out.println("Calculate total km.");
                totalKm = haversine(customerLat, customerLon, providerLat, providerLon);
                System.out.println("Total km : " + totalKm);
            } else {
                System.out.println("Calculate total km.");
                totalKm = haversine(providerLat, providerLon, customerLat, customerLon);
                System.out.println("Total km : " + totalKm);
            }

            if(totalKm > deliveryAgentEarnings.getBaseKm()){
                Double netKm = totalKm - deliveryAgentEarnings.getBaseKm();
                Double extraAmount = netKm * deliveryAgentEarnings.getExtraPerKmAmount();
                earning = deliveryAgentEarnings.getFixedAmount() + extraAmount;
            } else {
                earning = deliveryAgentEarnings.getFixedAmount();
            }

            //  Store earning and km in Redis for later use in bill generation
            Map<String, Object> deliveryInfo = new HashMap<>();
            System.out.println("Delivery earnings : " + deliveryInfo);
            deliveryInfo.put("earning", round(earning, 2));
            deliveryInfo.put("totalKm", round(totalKm, 2));
            redisTemplate.opsForHash().putAll("deliveryEarnings:" + order.getOrderId(), deliveryInfo);
            redisTemplate.expire("deliveryEarnings:" + order.getOrderId(), Duration.ofDays(30));

            String address = order.getServiceProvider().getUser().getAddress().getName() + " " + order.getServiceProvider().getUser().getAddress().getAreaName() +
                    " " + order.getServiceProvider().getUser().getAddress().getCity().getCityName() + " " + order.getServiceProvider().getUser().getAddress().getPincode();

            Long quantity = 0L;
            List<BookingItem> bookingItemList = bookingItemRepository.findByOrder(order);
            List<PendingDeliveriesResponseDTO.BookingItemDTO> bookingItemDTOS = new ArrayList<>();
            for(BookingItem item : bookingItemList){
                quantity += item.getQuantity();
                PendingDeliveriesResponseDTO.BookingItemDTO bookingItemDTO = PendingDeliveriesResponseDTO.BookingItemDTO.builder()
                        .itemName(item.getItem().getItemName())
                        .serviceName(item.getItem().getService() != null ?
                                item.getItem().getService().getServiceName() :
                                item.getItem().getSubService().getServices().getServiceName())
                        .quantity(item.getQuantity())
                        .build();
                bookingItemDTOS.add(bookingItemDTO);
            }

            String deliveryType;
            PendingDeliveriesResponseDTO pendingDeliveriesResponseDTO;

            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)){
                deliveryType = "Customer -> Service Provider";

                pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
                        .orderId(order.getOrderId())
                        .orderStatus(order.getStatus())
                        .deliveryType(deliveryType)
                        .deliveryEarning(round(earning, 2))
                        .km(round(totalKm, 2))
                        .pickupDate(order.getPickupDate())
                        .pickupTime(order.getPickupTime())
                        .pickupName(order.getContactName())
                        .pickupPhone(order.getContactPhone())
                        .pickupAddress(order.getContactAddress())
                        .deliveryName(order.getServiceProvider().getUser().getFirstName() + " "+ order.getServiceProvider().getUser().getLastName())
                        .deliveryPhone(order.getServiceProvider().getUser().getPhoneNo())
                        .deliveryAddress(address)
                        .totalQuantity(quantity)
                        .bookingItemDTOList(bookingItemDTOS)
                        .build();
            } else {
                deliveryType = "Service Provider -> Customer";

                pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
                        .orderId(order.getOrderId())
                        .orderStatus(order.getStatus())
                        .deliveryType(deliveryType)
                        .deliveryEarning(round(earning, 2))
                        .km(round(totalKm, 2))
                        .pickupDate(order.getDeliveryDate())
                        .pickupTime(order.getDeliveryTime())
                        .pickupName(order.getServiceProvider().getUser().getFirstName() + " "+ order.getServiceProvider().getUser().getLastName())
                        .pickupPhone(order.getServiceProvider().getUser().getPhoneNo())
                        .pickupAddress(address)
                        .deliveryName(order.getContactName())
                        .deliveryPhone(order.getContactPhone())
                        .deliveryAddress(order.getContactAddress())
                        .totalQuantity(quantity)
                        .bookingItemDTOList(bookingItemDTOS)
                        .build();
            }

            pendingDeliveriesResponseDTOList.add(pendingDeliveriesResponseDTO);
        }

        return pendingDeliveriesResponseDTOList;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Transactional
    public List<PendingDeliveriesResponseDTO> getTodayDeliveries(Users user) {
        List<PendingDeliveriesResponseDTO> pendingDeliveriesResponseDTOList = new ArrayList<>();

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));

        List<Order> order1 = orderRepository.findByPickupDeliveryAgentAndPickupDate(deliveryAgent, LocalDate.now());
        List<Order> order2 = orderRepository.findByDeliveryDeliveryAgentAndDeliveryDate(deliveryAgent, LocalDate.now());

        System.out.println("Pickup orders size : " + order1.size());
        System.out.println("Delivery orders size : " + order2.size());

        List<Order> orders = new ArrayList<>();
        orders.addAll(order1);
        orders.addAll(order2);

        for(Order order : orders){

            Bill bill = billRepository.findByOrder(order);
            if (bill == null) {
                logger.warn("No bill found for order ID: {}", order.getOrderId());
                continue;
            }

            String address = order.getServiceProvider().getUser().getAddress().getName() + " " + order.getServiceProvider().getUser().getAddress().getAreaName() +
                    " " + order.getServiceProvider().getUser().getAddress().getCity().getCityName() + " " + order.getServiceProvider().getUser().getAddress().getPincode();

            Long quantity = 0L;
            List<BookingItem> bookingItemList = bookingItemRepository.findByOrder(order);
            List<PendingDeliveriesResponseDTO.BookingItemDTO> bookingItemDTOS = new ArrayList<>();
            for(BookingItem item : bookingItemList){
                quantity += item.getQuantity();
                PendingDeliveriesResponseDTO.BookingItemDTO bookingItemDTO = PendingDeliveriesResponseDTO.BookingItemDTO.builder()
                        .itemName(item.getItem().getItemName())
                        .serviceName(item.getItem().getService() != null ? item.getItem().getService().getServiceName() : item.getItem().getSubService().getServices().getServiceName())
                        .quantity(item.getQuantity())
                        .build();
                bookingItemDTOS.add(bookingItemDTO);
            }

            String deliveryType;
            PendingDeliveriesResponseDTO pendingDeliveriesResponseDTO;

            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_AGENT) || order.getStatus().equals(OrderStatus.PICKED_UP)){

                if(!Objects.equals(order.getPickupDate(), LocalDate.now())) continue;

                deliveryType = "Customer -> Service Provider";

                System.out.println("**Pickup date : " + order.getPickupDate());
                System.out.println("**Pickup date : " + order.getPickupTime());
                pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
                        .orderId(order.getOrderId())
                        .orderStatus(order.getStatus())
                        .deliveryType(deliveryType)
                        .deliveryEarning(bill.getDeliveryCharge())
                        .km(order.getTotalKm())
                        .pickupDate(order.getPickupDate())
                        .pickupTime(order.getPickupTime())
                        .pickupName(order.getContactName())
                        .pickupPhone(order.getContactPhone())
                        .pickupAddress(order.getContactAddress())
                        .orderStatus(order.getStatus())
                        .deliveryName(order.getServiceProvider().getUser().getFirstName() + " "+ order.getServiceProvider().getUser().getLastName())
                        .deliveryPhone(order.getServiceProvider().getUser().getPhoneNo())
                        .deliveryAddress(address)
                        .totalQuantity(quantity)
                        .bookingItemDTOList(bookingItemDTOS)
                        .build();
            } else if(order.getStatus().equals(OrderStatus.READY_FOR_DELIVERY) || order.getStatus().equals(OrderStatus.OUT_FOR_DELIVERY)){

                if(!Objects.equals(order.getDeliveryDate(), LocalDate.now())) continue;

                deliveryType = "Service Provider -> Customer";

                pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
                        .orderId(order.getOrderId())
                        .orderStatus(order.getStatus())
                        .deliveryType(deliveryType)
                        .deliveryEarning(bill.getDeliveryCharge())
                        .km(order.getTotalKm())
                        .pickupDate(order.getDeliveryDate())
                        .pickupTime(order.getDeliveryTime())
                        .orderStatus(order.getStatus())
                        .pickupName(order.getServiceProvider().getUser().getFirstName() + " "+ order.getServiceProvider().getUser().getLastName())
                        .pickupPhone(order.getServiceProvider().getUser().getPhoneNo())
                        .pickupAddress(address)
                        .deliveryName(order.getContactName())
                        .deliveryPhone(order.getContactPhone())
                        .deliveryAddress(order.getContactAddress())
                        .totalQuantity(quantity)
                        .bookingItemDTOList(bookingItemDTOS)
                        .build();
            } else {
                continue;
            }

            pendingDeliveriesResponseDTOList.add(pendingDeliveriesResponseDTO);
        }

        return pendingDeliveriesResponseDTOList;
    }

    @Autowired
    AvailabilityService availabilityService;

    // Sort delivery agent based on distance of delivery agent from customer
    public void assignToDeliveryAgentCustomerOrders(String orderId) throws JsonProcessingException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not exists."));

        if(order.getStatus() != OrderStatus.ACCEPTED_BY_PROVIDER){
            throw new RuntimeException("Order is not accepted by service provider yet");
        }

        // Get customer lat/lng
        Double custLat = order.getLatitude();
        Double custLng = order.getLongitude();

        // Fetch rejected agent list
        @SuppressWarnings("unchecked")
        Set<String> rejectedAgentIds =  (Set<String>)(Set<?>) redisTemplate.opsForSet().members("rejectedAgents:" + orderId);
        if (rejectedAgentIds == null) rejectedAgentIds = Collections.emptySet();

        // Get all delivery agent IDs and locations from Redis
        Set<Object> activeAgents = redisTemplate.opsForSet().members("activeDeliveryAgents");

        if (activeAgents == null || activeAgents.isEmpty()) {
            logger.warn("No active delivery agents available. Cannot assign order: {}", orderId);
            return; // or throw exception, or mark as pending
        }

        Set<String> agentIds = activeAgents != null
                ? activeAgents.stream().map(Object::toString).collect(Collectors.toSet())
                : Collections.emptySet();

        Map<String, Double> agentDistances = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (String key : agentIds) {

            // Skip if already rejected
            if (rejectedAgentIds.contains(key)) continue;

            DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(key).orElse(null);

            // Check availability
//            boolean isAvailable = availabilityRepository.isAgentAvailable(agent.getDeliveryAgentId(), LocalDate.now(), LocalTime.now());
            boolean isAvailable = availabilityService.isCurrentlyAvailable(agent.getUsers().getUserId());
            if (!isAvailable) {
                logger.info("Agent {} is not available at {} on {}", agent.getDeliveryAgentId(), now, today);
                continue;
            }

            String locationKey = "deliveryAgentLocation:" + key;

            @SuppressWarnings("unchecked")
            Map<String, Double> loc = (Map<String, Double>) redisTemplate.opsForValue().get(locationKey);


            if (loc != null) {

                Double agentLat = loc.get("latitude");
                Double agentLng = loc.get("longitude");

                Double distance = haversine(custLat, custLng, agentLat, agentLng);
                agentDistances.put(key, distance);
            }
        }

        // Sort by distance and try to assign
        agentDistances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> tryAssign(order, entry.getKey()));
    }

    // Sort delivery agent based on distance of delivery agent from customer to delivery agent
    public void assignToDeliveryAgentServiceProviderOrders(String orderId) throws JsonProcessingException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not exists."));

        if(order.getStatus() != OrderStatus.READY_FOR_DELIVERY){
            throw new RuntimeException("Order is not accepted by service provider yet");
        }

        // Get customer lat/lng
        Double custLat = order.getServiceProvider().getUser().getAddress().getLatitude();
        Double custLng = order.getServiceProvider().getUser().getAddress().getLongitude();

        // Fetch rejected agent list
        @SuppressWarnings("unchecked")
        Set<String> rejectedAgentIds =  (Set<String>)(Set<?>) redisTemplate.opsForSet().members("rejectedAgents:" + orderId);
        if (rejectedAgentIds == null) rejectedAgentIds = Collections.emptySet();

        // Get all delivery agent IDs and locations from Redis
        Set<Object> activeAgents = redisTemplate.opsForSet().members("activeDeliveryAgents");

        if (activeAgents == null || activeAgents.isEmpty()) {
            logger.warn("No active delivery agents available. Cannot assign order: {}", orderId);
            return; // or throw exception, or mark as pending
        }

        Set<String> agentIds = activeAgents != null
                ? activeAgents.stream().map(Object::toString).collect(Collectors.toSet())
                : Collections.emptySet();

        Map<String, Double> agentDistances = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (String key : agentIds) {

            // Skip if already rejected
            if (rejectedAgentIds.contains(key)) continue;

            DeliveryAgent agent = deliveryAgentRepository.findByUsers_UserId(key).orElse(null);

            // Check availability
//            boolean isAvailable = availabilityRepository.isAgentAvailable(agent.getDeliveryAgentId(), LocalDate.now(), LocalTime.now());
            boolean isAvailable = availabilityService.isCurrentlyAvailable(agent.getUsers().getUserId());
            if (!isAvailable) {
                logger.info("Agent {} is not available at {} on {}", agent.getDeliveryAgentId(), now, today);
                continue;
            }

            String locationKey = "deliveryAgentLocation:" + key;

            @SuppressWarnings("unchecked")
            Map<String, Double> loc = (Map<String, Double>) redisTemplate.opsForValue().get(locationKey);


            if (loc != null) {

                Double agentLat = loc.get("latitude");
                Double agentLng = loc.get("longitude");

                Double distance = haversine(custLat, custLng, agentLat, agentLng);
                agentDistances.put(key, distance);
            }
        }

        // Sort by distance and try to assign
        agentDistances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> tryAssign(order, entry.getKey()));
    }

    // Calculates distance between two lat/lng points using Haversine formula.
    public double haversine(double custLat, double custLng, double agentLat, double agentLng) {
        final int R = 6371; // Radius of Earth in km

        double dLat = Math.toRadians(agentLat - custLat);
        double dLon = Math.toRadians(agentLng - custLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(custLat)) * Math.cos(Math.toRadians(agentLat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }

    private static final String ORDER_ASSIGNMENT_PREFIX = "assignment:";

    // Logic to assign orders to delivery agents
    public void tryAssign(Order order, String userId) {

        String redisKey = ORDER_ASSIGNMENT_PREFIX + order.getOrderId();

        // Check if already assigned
        if (redisTemplate.hasKey(redisKey)) return;

        // Build assignment data
        OrderAssignmentDTO assignment = new OrderAssignmentDTO(userId, OrderStatus.PENDING, System.currentTimeMillis());

//        String value = objectMapper.writeValueAsString(assignment);

        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, assignment);
        if (Boolean.TRUE.equals(success)) {
            scheduleReassignment(order.getOrderId(), userId);
            logger.info("Assigned order {} to agent {}", order.getOrderId(), userId);

            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found."));
            // Fetch delivery agent by agentId
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Delivery agent not found: " + userId ));

            // Send notification to delivery agent
//            smsService.sendOrderStatusNotification(
//                    deliveryAgent.getUsers().getPhoneNo(),
//                    "New order assigned to you from user " + order.getUsers().getFirstName()
//            );
            if(deliveryAgent.getUsers().getEmail() != null) {
                emailService.sendOrderStatusNotification(
                        deliveryAgent.getUsers().getEmail(),
                        "New Order Assigned",
                        "You have been assigned a new order from " + order.getUsers().getFirstName()
                );
            }
        }

    }

    //  Run if the agent hasn’t accepted within 5 minutes.
    private void scheduleReassignment(String orderId, String userId) {
        taskScheduler.schedule(() -> {
            String redisKey = "assignment:" + orderId;

            Object rawValue = redisTemplate.opsForValue().get(redisKey);
            System.out.println("Redis raw value: " + rawValue);
            if (rawValue != null) {
                String assignmentJson = rawValue.toString();
                try {
                    OrderAssignmentDTO assignment = objectMapper.readValue(assignmentJson, OrderAssignmentDTO.class);

                    // Check if still pending
                    if (assignment.getStatus() == OrderStatus.ACCEPTED_BY_PROVIDER || assignment.getStatus() == OrderStatus.READY_FOR_DELIVERY) {

                        // Add to rejected agents
                        redisTemplate.opsForSet().add("rejectedAgents:" + orderId, userId);

                        // Remove assignment key
                        redisTemplate.delete(redisKey);

                        // Retry assignment
                        assignToDeliveryAgentCustomerOrders(orderId);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
//        }, Instant.now().plus(5, ChronoUnit.SECONDS));
        }, Instant.now().plus(5, ChronoUnit.MINUTES));
    }

    // Logic for accept order
    public boolean acceptOrder(String orderId, String agentId) {
        String redisKey = "assignment:" + orderId;

        // Get current assignment info from Redis
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) return false;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> assignment;

            if (value instanceof String json) {
                assignment = mapper.readValue(json, new TypeReference<>() {});
            } else if (value instanceof Map<?, ?> rawMap) {
                //noinspection unchecked
                assignment = (Map<String, Object>) rawMap;
            } else {
                throw new RuntimeException("Unexpected Redis data type for assignment");
            }

            String assignedAgentId = (String) assignment.get("agentId");
            if (!assignedAgentId.equals(agentId)) return false;

            // Fetch order and agent
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order is not available."));
            Users user = userRepository.findById(agentId)
                    .orElseThrow(() -> new RuntimeException("User does not exist"));
            DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Agent not found"));

            // Update order status and agent
            if (order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)) {
                order.setPickupDeliveryAgent(agent);
                order.setStatus(OrderStatus.ACCEPTED_BY_AGENT);
            } else {
                order.setDeliveryDeliveryAgent(agent);
            }

            // Retrieve delivery data from Redis
            String deliveryRedisKey = "deliveryEarnings:" + orderId;
            Map<Object, Object> deliveryData = redisTemplate.opsForHash().entries(deliveryRedisKey);

            if (!deliveryData.isEmpty()) {
                Double totalKm = deliveryData.get("totalKm") != null
                        ? Double.parseDouble(deliveryData.get("totalKm").toString()) : null;
                Double earning = deliveryData.get("earning") != null
                        ? Double.parseDouble(deliveryData.get("earning").toString()) : null;

                // Save totalKm to Order
                if (totalKm != null) {
                    order.setTotalKm(totalKm);
                }

                // Save deliveryCharge to Bill
                if (earning != null) {
                    Bill bill = billRepository.findByOrder(order);

                    double totalDeliveryCharge = earning * 2; // pickup + delivery

                    if (bill == null) {
                        // Auto-create a minimal Bill if missing
                        bill = Bill.builder()
                                .order(order)
                                .status(BillStatus.PENDING)
                                .itemsTotalPrice(0.0)
                                .gstAmount(0.0)
                                .discountAmount(0.0)
                                .finalPrice(0.0)
                                .deliveryCharge(totalDeliveryCharge)
                                .build();
                        billRepository.save(bill);
                    } else if (bill.getDeliveryCharge() == null) {
                        bill.setDeliveryCharge(totalDeliveryCharge);
                        billRepository.save(bill);
                    }
                }

                redisTemplate.delete(deliveryRedisKey);
            }

            orderRepository.save(order);
            redisTemplate.delete(redisKey);

            return true;

        } catch (Exception e) {
            logger.error("Failed to accept order {} by agent {}: {}", orderId, agentId, e.getMessage(), e);
            return false;
        }
    }


    // this function is called in accept order of service provider
    public void calculateDeliveryChargeForProvider(Order order) {
        Double providerLat = order.getServiceProvider().getUser().getAddress().getLatitude();
        Double providerLon = order.getServiceProvider().getUser().getAddress().getLongitude();
        Double customerLat = order.getLatitude();
        Double customerLon = order.getLongitude();

        // Calculate total distance (km)
        Double totalKm = haversine(providerLat, providerLon, customerLat, customerLon);

        // Fetch delivery earnings configuration
        DeliveryAgentEarnings earnings = deliveryAgentEarningsRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
        if (earnings == null) {
            throw new IllegalStateException("No active earnings configuration found.");
        }

        // Calculate charge based on base km logic
        Double singleLegCharge;
        if (totalKm > earnings.getBaseKm()) {
            Double extraKm = totalKm - earnings.getBaseKm();
            singleLegCharge = earnings.getFixedAmount() + (extraKm * earnings.getExtraPerKmAmount());
        } else {
            singleLegCharge = earnings.getFixedAmount();
        }

        // Round off
        double roundedKm = round(totalKm, 2);
        double totalDeliveryCharge = round(singleLegCharge * 2, 2); // pickup + delivery

        // Update order's total km
        order.setTotalKm(roundedKm);

        // Get bill and apply delivery charge only if not set
        Bill bill = billRepository.findByOrder(order);
        if (bill == null) {
            bill = Bill.builder()
                    .order(order)
                    .status(BillStatus.PENDING)
                    .itemsTotalPrice(0.0)
                    .gstAmount(0.0)
                    .discountAmount(0.0)
                    .finalPrice(0.0)
                    .deliveryCharge(totalDeliveryCharge)
                    .build();
            billRepository.save(bill);
        } else if (bill.getDeliveryCharge() == null) {
            bill.setDeliveryCharge(totalDeliveryCharge);
            billRepository.save(bill);
        }

        // Note: If deliveryCharge is already set, we do not override it.
    }

    public String changeStatus(String orderId) {
        return "Status is updated successfully.";
    }

}