package com.SmartLaundry.repository;

import com.SmartLaundry.model.CurrentStatus;
import com.SmartLaundry.model.DeliveryAgentEarnings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAgentEarningsRepository extends JpaRepository<DeliveryAgentEarnings, Long> {

    DeliveryAgentEarnings findByCurrentStatus(CurrentStatus currentStatus);
}
