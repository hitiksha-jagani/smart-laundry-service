package com.SmartLaundry.repository;

import com.SmartLaundry.model.BankAccount;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.DeliveryAgentAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<DeliveryAgentAvailability, String> {
    List<DeliveryAgentAvailability> findByDeliveryAgentAndDateBetween(DeliveryAgent agent, LocalDate startOfWeek, LocalDate endOfWeek);
}