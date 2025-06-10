package com.SmartLaundry.service;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProviderAccORrejectOrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Get all pending approval orders for a service provider
    public List<Order> getPendingOrdersForProvider(String providerId) {
        return orderRepository.findByServiceProvider_User_UserIdAndStatus(providerId, OrderStatus.PENDING);
    }

    // Accept or reject an order
    public void respondToOrder(String orderId, String action) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order already processed");
        }

        if ("ACCEPT".equalsIgnoreCase(action)) {
            order.setStatus(OrderStatus.ACCEPTED);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            order.setStatus(OrderStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }

        orderRepository.save(order);
    }

}

