package com.SmartLaundry.repository;

import com.SmartLaundry.model.CurrentStatus;
import com.SmartLaundry.model.RevenueBreakDown;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueBreakDownRepository extends JpaRepository<RevenueBreakDown, Long> {
    RevenueBreakDown findByCurrentStatus(CurrentStatus currentStatus);
}
