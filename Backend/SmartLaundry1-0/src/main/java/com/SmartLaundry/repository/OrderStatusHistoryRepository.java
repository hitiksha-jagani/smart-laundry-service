package com.SmartLaundry.repository;

import com.SmartLaundry.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
}

