package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliverySummaryResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderAssignmentDTO;
import com.SmartLaundry.dto.DeliveryAgent.OrderResponseDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

// @author Hitiksha Jagani
@Service
public class DeliveriesService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private DeliveriesRepository deliveriesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingItemRepository bookingItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public DeliverySummaryResponseDTO deliveriesSummary(String agentId) {
        Users user = userRepository.findById(agentId).orElseThrow();
        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElseThrow();

        DeliverySummaryResponseDTO deliverySummaryResponseDTO = new DeliverySummaryResponseDTO();
        deliverySummaryResponseDTO.setTotalDeliveries(deliveriesRepository.countDeliveriesByAgent(deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setPendingDeliveries(countPendingAssignmentsForAgent(deliveryAgent.getDeliveryAgentId()));
        deliverySummaryResponseDTO.setUpcomingDeliveries(deliveriesRepository.countAcceptedOrdersForToday(LocalDate.now(), deliveryAgent.getDeliveryAgentId()));

        return deliverySummaryResponseDTO;
    }

    // Count pending orders
    public int countPendingAssignmentsForAgent(String agentId) {
        int count = 0;
        Set<String> keys = redisTemplate.keys("assignment:*");

        if (keys == null || keys.isEmpty()) return 0;

        for (String key : keys) {
            try {
                String json = (String) redisTemplate.opsForValue().get(key);
                if (json == null) continue;

                OrderAssignmentDTO dto = objectMapper.readValue(json, OrderAssignmentDTO.class);
                if (dto.getAgentId().equals(agentId) && dto.getStatus() == OrderStatus.PENDING) {
                    count++;
                }

            } catch (Exception e) {
                System.err.println("Failed to parse value for key: " + key);
            }
        }

        return count;
    }

    // Sort delivery agent based on distance of delivery agent from customer
    public void assignToDeliveryAgent(String orderId) throws JsonProcessingException {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Users customer = order.getUsers();

        // Get customer lat/lng
        Double custLat = order.getLatitude();
        Double custLng = order.getLongitude();

        // Fetch rejected agent list
        @SuppressWarnings("unchecked")
        Set<String> rejectedAgentIds =  (Set<String>)(Set<?>) redisTemplate.opsForSet().members("rejectedAgents:" + orderId);
        if (rejectedAgentIds == null) rejectedAgentIds = Collections.emptySet();

        // Get all delivery agent IDs and locations from Redis
        Set<String> keys = redisTemplate.keys("deliveryAgentLocation:*");
        Map<String, Double> agentDistances = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        for (String key : keys) {
            String agentId = key.split(":")[1];

            // Skip if already rejected
            if (rejectedAgentIds.contains(agentId)) continue;

            String loc = (String) redisTemplate.opsForValue().get(key);
            if (loc != null) {

                Map<String, Double> location = objectMapper.readValue(loc, new TypeReference<>() {});
                double agentLat = location.get("latitude");
                double agentLng = location.get("longitude");

                double distance = haversine(custLat, custLng, agentLat, agentLng);
                agentDistances.put(agentId, distance);
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
            redisTemplate.opsForValue().set(redisKey, value, 5, TimeUnit.MINUTES);

            // Schedule a fallback if order is not accepted in time
            scheduleReassignment(order.getOrderId(), agentId);

            // Send notification or request to agent
            System.out.println("Order " + order.getOrderId() + " sent to Agent " + agentId);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to store assignment", e);
        }
    }

    //  Run if the agent hasn’t accepted within 5 minutes.
    private void scheduleReassignment(String orderId, String agentId) {
        taskScheduler.schedule(() -> {
            String redisKey = "assignment:" + orderId;

            // Check if order is still not accepted
            if (redisTemplate.hasKey(redisKey)) {
                // Add agent to rejected set
                redisTemplate.opsForSet().add("rejectedAgents:" + orderId, agentId);

                // Delete old assignment
                redisTemplate.delete(redisKey);

                // Re-run assignment logic
                try {
                    assignToDeliveryAgent(orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Instant.now().plus(5, ChronoUnit.MINUTES)); // trigger after 5 min
    }

    // Logic for accept order
    public boolean acceptOrder(String orderId, String agentId) {
        String redisKey = "assignment:" + orderId;

        // Get current assignment info from Redis
        String value = (String) redisTemplate.opsForValue().get(redisKey);
        if (value == null) return false;

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> assignment = mapper.readValue(value, new TypeReference<>() {});
            String assignedAgentId = (String) assignment.get("agentId");

            // Check if the accepting agent is the same one assigned
            if (!assignedAgentId.equals(agentId)) {
                return false;
            }

            // Update order in DB
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order is not available."));

            Users user = userRepository.findById(agentId)
                    .orElseThrow(() -> new RuntimeException("User is not exist"));

            DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                    .orElseThrow(() -> new RuntimeException("Agent not found"));

            order.setDeliveryAgent(agent);
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            // Remove from Redis
            redisTemplate.delete(redisKey);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderResponseDTO> todaysDeliveries(String userId) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"DELIVERY_AGENT".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        LocalDate pickupDate = LocalDate.now();

        List<Order> orders = orderRepository.findByPickupDate(pickupDate);
        List<OrderResponseDTO> responseList = new ArrayList<>();

        for (Order odr : orders) {

            List<BookingItem> bookingItems = bookingItemRepository.findByOrder(odr);
            List<OrderResponseDTO.BookingItemDTO> bookingItemDTOList = new ArrayList<>();

            long totalQuantity = 0;

            for (BookingItem items : bookingItems) {
                OrderResponseDTO.BookingItemDTO bookingDTO = OrderResponseDTO.BookingItemDTO.builder()
                        .itemName(items.getItem().getItemName())
                        .serviceName(items.getItem().getService().getServiceName())
                        .quantity(items.getQuantity())
                        .build();

                totalQuantity += items.getQuantity();
                bookingItemDTOList.add(bookingDTO);
            }

            String customerAdd = user.getAddress().getName() + user.getAddress().getAreaName() + user.getAddress().getCity().getCityName() + user.getAddress().getPincode();
            String providerAdd = odr.getServiceProvider().getUser().getAddress().getName() +
                    odr.getServiceProvider().getUser().getAddress().getAreaName() + odr.getServiceProvider().getUser().getAddress().getPincode() +
                    odr.getServiceProvider().getUser().getAddress().getCity().getCityName();

            OrderResponseDTO orderResponse = OrderResponseDTO.builder()
                    .orderId(odr.getOrderId())
                    .customerName(odr.getUsers().getFirstName() + " " + odr.getUsers().getLastName())
                    .customerPhone(odr.getUsers().getPhoneNo())
                    .customerAddress(customerAdd)
                    .providerName(odr.getServiceProvider().getUser().getFirstName() + odr.getServiceProvider().getUser().getLastName())
                    .providerPhone(odr.getServiceProvider().getUser().getPhoneNo())
                    .providerAddress(providerAdd)
                    .totalQuantity(totalQuantity)
                    .bookingItemDTOList(bookingItemDTOList)
                    .build();

            responseList.add(orderResponse);
        }

        return responseList;
    }
}
