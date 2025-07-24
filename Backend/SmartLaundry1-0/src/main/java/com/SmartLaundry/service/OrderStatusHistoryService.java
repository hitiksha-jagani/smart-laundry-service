package com.SmartLaundry.service;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.OrderStatusHistory;
import com.SmartLaundry.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStatusHistoryService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    public void save(Order order, OrderStatus newStatus) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .changedAt(LocalDateTime.now())
                .build();
        orderStatusHistoryRepository.save(history);
    }

    public List<OrderStatusHistory> getHistoryForOrder(String orderId) {
        return orderStatusHistoryRepository.findByOrderOrderIdOrderByChangedAtDesc(orderId);
    }
}
