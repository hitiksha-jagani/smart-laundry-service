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
import jakarta.mail.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
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
        deliverySummaryResponseDTO.setTotalDeliveries(deliveriesRepository.countDeliveriesByAgent(deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setPendingDeliveries(countPendingAssignmentsForAgent(deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setTodayDeliveries(
                deliveriesRepository.countAcceptedDeliveryOrdersForToday(LocalDate.now(), deliveryAgent.getDeliveryAgentId()) +
                        deliveriesRepository.countAcceptedPickupOrdersForToday(LocalDate.now(), deliveryAgent.getDeliveryAgentId()));

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
//    public List<PendingDeliveriesResponseDTO> pendingDeliveries(Users user) {
//
//        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
//                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));
//        List<Order> order1 = orderRepository.findByStatusAndPickupDeliveryAgent(OrderStatus.ACCEPTED_BY_PROVIDER, deliveryAgent);
//        List<Order> order2 = orderRepository.findByStatusAndDeliveryDeliveryAgent(OrderStatus.READY_FOR_DELIVERY, deliveryAgent);
//        List<Order> orders = new ArrayList<>();
//        if(order1 != null) orders.addAll(order1);
//        if(order2 != null) orders.addAll(order2);
//        List<PendingDeliveriesResponseDTO> pendingDeliveriesResponseDTOList = new ArrayList<>();
//
//        Double agentLat, agentLon, providerLat, providerLon, customerLat, customerLon, earning;
//
//        DeliveryAgentEarnings deliveryAgentEarnings = deliveryAgentEarningsRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
//        if (deliveryAgentEarnings == null) {
//            throw new IllegalStateException("No active earnings settings found.");
//        }
//
//        for(Order order : orders){
//            providerLat = order.getServiceProvider().getUser().getAddress().getLatitude();
//            providerLon = order.getServiceProvider().getUser().getAddress().getLongitude();
//            customerLat = order.getLatitude();
//            customerLon = order.getLongitude();
//
//            String locationKey = "deliveryAgentLocation:" + user.getUserId();
//
//            @SuppressWarnings("unchecked")
//            Map<String, Double> loc = (Map<String, Double>) redisTemplate.opsForValue().get(locationKey);
//
//            if (loc != null && loc.get("latitude") != null && loc.get("longitude") != null) {
//                agentLat = loc.get("latitude");
//                agentLon = loc.get("longitude");
//            } else {
//                continue;
//            }
//
//            Double distAgentToCustomer = haversine(agentLat, agentLon, customerLat, customerLon);
//            Double distCustomerToProvider = haversine(customerLat, customerLon, providerLat, providerLon);
//            double totalKm = distAgentToCustomer + distCustomerToProvider;
//
//            if(totalKm > deliveryAgentEarnings.getBaseKm()){
//                Double netKm = totalKm - deliveryAgentEarnings.getBaseKm();
//                Double extraAnount = netKm * deliveryAgentEarnings.getExtraPerKmAmount();
//                earning = deliveryAgentEarnings.getFixedAmount() + extraAnount;
//            } else {
//                earning = deliveryAgentEarnings.getFixedAmount();
//            }
//
//            String address = order.getServiceProvider().getUser().getAddress().getName() + " " + order.getServiceProvider().getUser().getAddress().getAreaName() +
//                    " " + order.getServiceProvider().getUser().getAddress().getCity().getCityName() + " " + order.getServiceProvider().getUser().getAddress().getPincode();
//
//            Long quantity = 0L;
//            List<BookingItem> bookingItemList = bookingItemRepository.findByOrder(order);
//            List<PendingDeliveriesResponseDTO.BookingItemDTO> bookingItemDTOS = new ArrayList<>();
//            for(BookingItem item : bookingItemList){
//                quantity += item.getQuantity();
//                PendingDeliveriesResponseDTO.BookingItemDTO bookingItemDTO = PendingDeliveriesResponseDTO.BookingItemDTO.builder()
//                        .itemName(item.getItem().getItemName())
//                        .serviceName(item.getItem().getService() != null ? item.getItem().getService().getServiceName() : item.getItem().getSubService().getServices().getServiceName())
//                        .quantity(item.getQuantity())
//                        .build();
//                bookingItemDTOS.add(bookingItemDTO);
//            }
//
//            String deliveryType;
//
//            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)) {
//                deliveryType = "Customer -> Service Provider";
//            } else {
//                deliveryType = "Service Provider -> Customer";
//            }
//
//            PendingDeliveriesResponseDTO pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
//                    .orderId(order.getOrderId())
//                    .deliveryType(deliveryType)
//                    .deliveryEarning(round(earning, 2))
//                    .km(round(totalKm, 2))
//                    .customerName(order.getContactName())
//                    .customerPhone(order.getContactPhone())
//                    .customerAddress(order.getContactAddress())
//                    .providerName(order.getServiceProvider().getUser().getFirstName() + order.getServiceProvider().getUser().getLastName())
//                    .providerPhone(order.getServiceProvider().getUser().getPhoneNo())
//                    .providerAddress(address)
//                    .totalQuantity(quantity)
//                    .bookingItemDTOList(bookingItemDTOS)
//                    .build();
//
//            pendingDeliveriesResponseDTOList.add(pendingDeliveriesResponseDTO);
//
//        }
//
//        return pendingDeliveriesResponseDTOList;
//    }
    public List<PendingDeliveriesResponseDTO> pendingDeliveries(Users user) {

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));

        List<Order> order1 = orderRepository.findByStatusAndPickupDeliveryAgent(OrderStatus.ACCEPTED_BY_PROVIDER, deliveryAgent);
        List<Order> order2 = orderRepository.findByStatusAndDeliveryDeliveryAgent(OrderStatus.READY_FOR_DELIVERY, deliveryAgent);
        List<Order> orders = new ArrayList<>();
        if(order1 != null) orders.addAll(order1);
        if(order2 != null) orders.addAll(order2);
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

            String locationKey = "deliveryAgentLocation:" + user.getUserId();

            @SuppressWarnings("unchecked")
            Map<String, Double> loc = (Map<String, Double>) redisTemplate.opsForValue().get(locationKey);

            if (loc != null && loc.get("latitude") != null && loc.get("longitude") != null) {
                agentLat = loc.get("latitude");
                agentLon = loc.get("longitude");
            } else {
                continue;
            }

            Double distAgentToCustomer = haversine(agentLat, agentLon, customerLat, customerLon);
            Double distCustomerToProvider = haversine(customerLat, customerLon, providerLat, providerLon);
            double totalKm = distAgentToCustomer + distCustomerToProvider;

            if(totalKm > deliveryAgentEarnings.getBaseKm()){
                Double netKm = totalKm - deliveryAgentEarnings.getBaseKm();
                Double extraAmount = netKm * deliveryAgentEarnings.getExtraPerKmAmount();
                earning = deliveryAgentEarnings.getFixedAmount() + extraAmount;
            } else {
                earning = deliveryAgentEarnings.getFixedAmount();
            }

            //  Store earning and km in Redis for later use in bill generation
            Map<String, Object> deliveryInfo = new HashMap<>();
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

            String deliveryType = order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)
                    ? "Customer -> Service Provider"
                    : "Service Provider -> Customer";

            PendingDeliveriesResponseDTO pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
                    .orderId(order.getOrderId())
                    .deliveryType(deliveryType)
                    .deliveryEarning(round(earning, 2))
                    .km(round(totalKm, 2))
                    .customerName(order.getContactName())
                    .customerPhone(order.getContactPhone())
                    .customerAddress(order.getContactAddress())
                    .providerName(order.getServiceProvider().getUser().getFirstName() + order.getServiceProvider().getUser().getLastName())
                    .providerPhone(order.getServiceProvider().getUser().getPhoneNo())
                    .providerAddress(address)
                    .totalQuantity(quantity)
                    .bookingItemDTOList(bookingItemDTOS)
                    .build();

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

    public List<PendingDeliveriesResponseDTO> getTodayDeliveries(Users user) {
        List<PendingDeliveriesResponseDTO> pendingDeliveriesResponseDTOList = new ArrayList<>();

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));

        List<Order> order1 = orderRepository.findByStatusAndPickupDeliveryAgentAndPickupDate(OrderStatus.ACCEPTED_BY_AGENT, deliveryAgent, LocalDate.now());
        List<Order> order2 = orderRepository.findByStatusAndDeliveryDeliveryAgentAndDeliveryDate(OrderStatus.READY_FOR_DELIVERY, deliveryAgent, LocalDate.now());
        List<Order> orders = new ArrayList<>();
        orders.addAll(order1);
        orders.addAll(order2);

        for(Order order : orders){

            Bill bill = billRepository.findByOrder(order);

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
            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_AGENT)) {
                deliveryType = "Customer -> Service Provider";
            } else {
                deliveryType = "Service Provider -> Customer";
            }

            PendingDeliveriesResponseDTO pendingDeliveriesResponseDTO = PendingDeliveriesResponseDTO.builder()
                    .orderId(order.getOrderId())
                    .deliveryType(deliveryType)
                    .deliveryEarning(bill.getDeliveryCharge())
                    .km(order.getTotalKm())
                    .customerName(order.getContactName())
                    .customerPhone(order.getContactPhone())
                    .customerAddress(order.getContactAddress())
                    .providerName(order.getServiceProvider().getUser().getFirstName() + order.getServiceProvider().getUser().getLastName())
                    .providerPhone(order.getServiceProvider().getUser().getPhoneNo())
                    .providerAddress(address)
                    .totalQuantity(quantity)
                    .bookingItemDTOList(bookingItemDTOS)
                    .build();

            pendingDeliveriesResponseDTOList.add(pendingDeliveriesResponseDTO);
        }

        return pendingDeliveriesResponseDTOList;
    }

    // Sort delivery agent based on distance of delivery agent from customer
    public void assignToDeliveryAgent(String orderId) throws JsonProcessingException {

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

            // Check availability
            boolean isAvailable = availabilityRepository.isAgentAvailable(key, LocalDate.now(), LocalTime.now());
            if (!isAvailable) {
                logger.info("Agent {} is not available at {} on {}", key, now, today);
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
    public void tryAssign(Order order, String agentId) {

        String redisKey = ORDER_ASSIGNMENT_PREFIX + order.getOrderId();

        // Check if already assigned
        if (redisTemplate.hasKey(redisKey)) return;

        // Build assignment data
        OrderAssignmentDTO assignment = new OrderAssignmentDTO(agentId, OrderStatus.PENDING, System.currentTimeMillis());

        try {
            String value = objectMapper.writeValueAsString(assignment);

            Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, value);
            if (Boolean.TRUE.equals(success)) {
                scheduleReassignment(order.getOrderId(), agentId);
                logger.info("Assigned order {} to agent {}", order.getOrderId(), agentId);
            }

            // Fetch delivery agent by agentId
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(agentId)
                    .orElseThrow(() -> new RuntimeException("Delivery agent not found: " + agentId));

            // Send notification to delivery agent
            smsService.sendOrderStatusNotification(
                    deliveryAgent.getUsers().getPhoneNo(),
                    "New order assigned to you from user " + order.getUsers().getFirstName()
            );
            if(deliveryAgent.getUsers().getEmail() != null) {
                emailService.sendOrderStatusNotification(
                        deliveryAgent.getUsers().getEmail(),
                        "New Order Assigned",
                        "You have been assigned a new order from " + order.getUsers().getFirstName()
                );
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to store assignment", e);
        }
    }

    //  Run if the agent hasn’t accepted within 5 minutes.
    private void scheduleReassignment(String orderId, String agentId) {
        taskScheduler.schedule(() -> {
            String redisKey = "assignment:" + orderId;

            String assignmentJson = redisTemplate.opsForValue().get(redisKey).toString();
            if (assignmentJson != null) {
                try {
                    OrderAssignmentDTO assignment = objectMapper.readValue(assignmentJson, OrderAssignmentDTO.class);

                    // Check if still pending
                    if (assignment.getStatus() == OrderStatus.PENDING) {

                        // Add to rejected agents
                        redisTemplate.opsForSet().add("rejectedAgents:" + orderId, agentId);

                        // Remove assignment key
                        redisTemplate.delete(redisKey);

                        // Retry assignment
                        assignToDeliveryAgent(orderId);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }, Instant.now().plus(5, ChronoUnit.MINUTES)); // trigger after 5 min
    }

    // Logic for accept order
//    public boolean acceptOrder(String orderId, String agentId) {
//        String redisKey = "assignment:" + orderId;
//
//        // Get current assignment info from Redis
//        String value = (String) redisTemplate.opsForValue().get(redisKey);
//        if (value == null) return false;
//
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> assignment = mapper.readValue(value, new TypeReference<>() {});
//            String assignedAgentId = (String) assignment.get("agentId");
//
//            // Check if the accepting agent is the same one assigned
//            if (!assignedAgentId.equals(agentId)) {
//                return false;
//            }
//
//            // Update order in DB
//            Order order = orderRepository.findById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order is not available."));
//
//            Users user = userRepository.findById(agentId)
//                    .orElseThrow(() -> new RuntimeException("User is not exist"));
//
//            DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
//                    .orElseThrow(() -> new RuntimeException("Agent not found"));
//
//            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)) {
//                order.setPickupDeliveryAgent(agent);
//            } else {
//                order.setDeliveryDeliveryAgent(agent);
//            }
//
//            order.setStatus(OrderStatus.ACCEPTED_BY_AGENT);
//            orderRepository.save(order);
//
//            // Remove from Redis
//            redisTemplate.delete(redisKey);
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    public boolean acceptOrder(String orderId, String agentId) {
        String redisKey = "assignment:" + orderId;

        // Get current assignment info from Redis
        String value = (String) redisTemplate.opsForValue().get(redisKey);
        if (value == null) return false;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> assignment = mapper.readValue(value, new TypeReference<>() {});
            String assignedAgentId = (String) assignment.get("agentId");

            // Validate agent match
            if (!assignedAgentId.equals(agentId)) return false;

            // Fetch order and agent
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order is not available."));
            Users user = userRepository.findById(agentId)
                    .orElseThrow(() -> new RuntimeException("User does not exist"));
            DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Agent not found"));

            // Assign delivery agent based on status
            if (order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)) {
                order.setPickupDeliveryAgent(agent);
            } else {
                order.setDeliveryDeliveryAgent(agent);
            }

            order.setStatus(OrderStatus.ACCEPTED_BY_AGENT);

            //  Retrieve delivery data from Redis
            String deliveryRedisKey = "deliveryEarnings:" + orderId;
            Map<Object, Object> deliveryData = redisTemplate.opsForHash().entries(deliveryRedisKey);

            if (!deliveryData.isEmpty()) {
                Double totalKm = deliveryData.get("totalKm") != null
                        ? Double.parseDouble(deliveryData.get("totalKm").toString()) : null;
                Double earning = deliveryData.get("earning") != null
                        ? Double.parseDouble(deliveryData.get("earning").toString()) : null;

                //  Store totalKm to Order
                if (totalKm != null) {
                    order.setTotalKm(totalKm);
                }

                //  Store earning to Bill
                if (earning != null) {
                    Bill bill = billRepository.findByOrder(order);
//                            .orElseThrow(() -> new RuntimeException("Bill not found for order: " + orderId));
                    bill.setDeliveryCharge(earning);
                    billRepository.save(bill);
                }

                //  Remove delivery data from Redis
                redisTemplate.delete(deliveryRedisKey);
            }

            orderRepository.save(order);
            redisTemplate.delete(redisKey); // Remove assignment info

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public String changeStatus(String orderId) {
        return "Status is updated successfully.";
    }

}
