package com.SmartLaundry.repository;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    Optional<OrderStatusHistory> findTopByOrderOrderByChangedAtDesc(Order order);
    List<OrderStatusHistory> findByOrderOrderIdOrderByChangedAtDesc(String orderId);
}

