package com.SmartLaundry.repository;

import com.SmartLaundry.model.OrderSchedulePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSchedulePlanRepository extends JpaRepository<OrderSchedulePlan, Long> {
    // Add custom queries if needed later
}

