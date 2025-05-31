package com.SmartLaundry.repository;

import com.SmartLaundry.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByOrderByOrderIdAsc(); //ODR00001
}
