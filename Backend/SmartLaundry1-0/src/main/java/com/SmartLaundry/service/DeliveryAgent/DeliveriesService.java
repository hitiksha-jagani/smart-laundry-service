package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliverySummaryResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderAssignmentDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.PendingDeliveriesResponseDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.SMSService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private RoleCheckingService roleCheckingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

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
    public List<PendingDeliveriesResponseDTO> pendingDeliveries(Users user) throws JsonProcessingException {

        Set<String> keys = redisTemplate.keys("assignment:*");
        List<Order> orders = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {

                try {
                    String orderId = key.split(":")[1];
                    System.out.println("orderId : " + orderId);

                    Object value = redisTemplate.opsForValue().get(key);

                    OrderAssignmentDTO assignment = null;

                    if (value instanceof String json) {
                        assignment = objectMapper.readValue(json, OrderAssignmentDTO.class);
                    } else if (value instanceof LinkedHashMap map) {
                        assignment = objectMapper.convertValue(map, OrderAssignmentDTO.class);
                    }

                    if (assignment != null && assignment.getAgentId().equals(user.getUserId())) {
                        // Only include assignments for this delivery agent
                        Order order = orderRepository.findById(orderId)
                                .orElse(null);
                        if (order != null) {
                            orders.add(order);
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Failed to parse assignment for key " + key);
                    e.printStackTrace();
                }
            }
        }

        List<PendingDeliveriesResponseDTO> pendingDeliveriesResponseDTOList = new ArrayList<>();

        Double agentLat, agentLon, providerLat, providerLon, customerLat, customerLon, earning;

        DeliveryAgentEarnings deliveryAgentEarnings = deliveryAgentEarningsRepository.findByCurrentStatus(CurrentStatus.ACTIVE);
        if (deliveryAgentEarnings == null) {
            throw new IllegalStateException("No active earnings settings found.");
        }

        for(Order order : orders){
            System.out.println("orderID : " + order.getOrderId());
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
                Double extraAnount = netKm * deliveryAgentEarnings.getExtraPerKmAmount();
                earning = deliveryAgentEarnings.getFixedAmount() + extraAnount;
            } else {
                earning = deliveryAgentEarnings.getFixedAmount();
            }

            // Store earning and km in Redis for later use in bill generation
            Map<String, Object> deliveryInfo = new HashMap<>();
            deliveryInfo.put("earning", round(earning, 2));
            deliveryInfo.put("totalKm", round(totalKm, 2));
            redisTemplate.opsForHash().putAll("deliveryEarnings:" + order.getOrderId(), deliveryInfo);
            redisTemplate.expire("deliveryEarnings:" + order.getOrderId(), Duration.ofDays(1));

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

            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)) {
                deliveryType = "Customer -> Service Provider";
            } else {
                deliveryType = "Service Provider -> Customer";
            }

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
            System.out.println("Ok");
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
        System.out.println("Rejected agents for order " + orderId + ":");
        rejectedAgentIds.forEach(System.out::println);

        if (rejectedAgentIds == null) rejectedAgentIds = Collections.emptySet();

        // Get all delivery agent IDs and locations from Redis
        Set<Object> activeAgents = redisTemplate.opsForSet().members("activeDeliveryAgents");

        System.out.println("Active agents in Redis:");
        activeAgents.forEach(System.out::println);


        if (activeAgents == null || activeAgents.isEmpty()) {
            logger.warn("No active delivery agents available. Cannot assign order: {}", orderId);
        }

//        Set<String> agentIds = activeAgents != null
//                ? activeAgents.stream().map(Object::toString).collect(Collectors.toSet())
//                : Collections.emptySet();

        Set<String> agentIds = activeAgents != null
                ? activeAgents.stream()
                .map(Object::toString)
                .map(id -> id.replaceAll("^\"|\"$", "")) // <-- Remove surrounding quotes
                .collect(Collectors.toSet())
                : Collections.emptySet();


        Map<String, Double> agentDistances = new HashMap<>();

        for (String key : agentIds) {

            // Skip if already rejected
            if (rejectedAgentIds.contains(key)) continue;

            // Check availability
//            boolean isAvailable = availabilityRepository.isAgentAvailable(key, LocalDate.now(), LocalTime.now());
//            if (!isAvailable) {
//                logger.info("Agent {} is not available at {} on {}", key, now, today);
//                continue;
//            }

            String locationKey = "deliveryAgentLocation:" + key;

            @SuppressWarnings("unchecked")
            Map<String, Double> loc = (Map<String, Double>) redisTemplate.opsForValue().get(locationKey);


            if (loc != null) {

                Double agentLat = loc.get("latitude");
                Double agentLng = loc.get("longitude");

                Double distance = haversine(custLat, custLng, agentLat, agentLng);
                System.out.println("Distances to agents:");

                agentDistances.put(key, distance);
            }
        }

        agentDistances.forEach((agentId, distance) ->
                System.out.println(agentId + " => " + distance + " km")
        );

        // Sort by distance and try to assign
        agentDistances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .filter(agentId -> tryAssign(order, agentId))
                .findFirst(); // stops at first successful assignment
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
    public boolean tryAssign(Order order, String userId) {

        System.out.println("Enter to the assign");
        String redisKey = ORDER_ASSIGNMENT_PREFIX + order.getOrderId();

        // Check if already assigned
        if (redisTemplate.hasKey(redisKey)) return false;

        // Build assignment data
        OrderAssignmentDTO assignment = new OrderAssignmentDTO(userId, OrderStatus.PENDING, System.currentTimeMillis());

        //            String value = objectMapper.writeValueAsString(assignment);

        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, assignment);

        if (Boolean.TRUE.equals(success)) {
            scheduleReassignment(order.getOrderId(), userId);
            logger.info("Assigned order {} to agent {}", order.getOrderId(), userId);

            // Fetch delivery agent by agentId
            Users user = roleCheckingService.checkUser(userId);
            DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Delivery agent not found: "));

            System.out.println("Order assigned to delivery agent : " + deliveryAgent.getDeliveryAgentId() + " " + deliveryAgent.getUsers().getFirstName());

            // Send notification to delivery agent
//            smsService.sendOrderStatusNotification(
//                    deliveryAgent.getUsers().getPhoneNo(),
//                    "New order assigned to you from user " + order.getUsers().getFirstName()
//            );
//            if(deliveryAgent.getUsers().getEmail() != null) {
//                emailService.sendOrderStatusNotification(
//                        deliveryAgent.getUsers().getEmail(),
//                        "New Order Assigned",
//                        "You have been assigned a new order from " + order.getUsers().getFirstName()
//                );
//            }
            return true;
        }
        return false;
    }

    //  Run if the agent hasn’t accepted within 5 minutes.
    private void scheduleReassignment(String orderId, String agentId) {
        taskScheduler.schedule(() -> {
            String redisKey = "assignment:" + orderId;

            // Directly get deserialized object from Redis
            OrderAssignmentDTO assignment = (OrderAssignmentDTO) redisTemplate.opsForValue().get(redisKey);

            if (assignment != null) {
                // Check if still pending
                if (assignment.getStatus() == OrderStatus.PENDING) {
                    System.out.println("Reassigning order " + orderId + " because agent " + agentId + " didn't respond in time.");

                    // Add to rejected agents
                    redisTemplate.opsForSet().add("rejectedAgents:" + orderId, agentId);

                    // Remove assignment key
                    redisTemplate.delete(redisKey);

                    // Retry assignment
                    try {
                        assignToDeliveryAgent(orderId);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, Instant.now().plus(5, ChronoUnit.MINUTES)); // trigger after 5 min
    }

    // Logic for accept order
    public void acceptOrder(String orderId, String userId) {

        String redisKey = "assignment:" + orderId;
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            System.out.println("No Redis assignment found for key: " + redisKey);
            return;
        }

        try {
            OrderAssignmentDTO assignment = null;

            if (value instanceof String json) {
                assignment = objectMapper.readValue(json, OrderAssignmentDTO.class);
                System.out.println("Assignment id : " + assignment.getAgentId());
            } else if (value instanceof LinkedHashMap map) {
                assignment = objectMapper.convertValue(map, OrderAssignmentDTO.class);
            }

            // Check if the accepting agent is the same one assigned
            if (assignment != null && !assignment.getAgentId().equals(userId))  {
                System.out.println("Same agent trying to accept: " + userId);
                return;
            }

            // Update order in DB
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order is not available."));


            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User is not exist"));

            DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Agent not found"));

            if(order.getStatus().equals(OrderStatus.ACCEPTED_BY_PROVIDER)) {
                    order.setPickupDeliveryAgent(agent);
                    order.setStatus(OrderStatus.ACCEPTED_BY_AGENT);
            } else {
                    order.setDeliveryDeliveryAgent(agent);
                    order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
            }

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

            OrderStatusHistory orderStatusHistory = OrderStatusHistory.builder()
                    .status(order.getStatus())
                    .order(order)
                    .build();

            orderStatusHistoryRepository.save(orderStatusHistory);

            // Remove from Redis
            redisTemplate.delete(redisKey);

        } catch (Exception e) {
            System.out.println("Failed to parse assignment for key " + redisKey);
            e.printStackTrace();
        }
    }

    public String changeStatus(String orderId) {
        return "Status is updated successfully.";
    }

}
