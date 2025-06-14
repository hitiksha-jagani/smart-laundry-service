package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderHistoryService {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // Fetch all thr orders completes by delivery agent.
    public List<Order> completedOrders(Users user){
        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElseThrow();
        List<Order> order1 = orderRepository.findByPickupDeliveryAgent(deliveryAgent);
        List<Order> order2 = orderRepository.findByDeliveryDeliveryAgent(deliveryAgent);
        List<Order> orders = new ArrayList<>();
        orders.addAll(order1);
        orders.addAll(order2);

        return orders;
    }
}
