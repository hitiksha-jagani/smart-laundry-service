package com.SmartLaundry.repository;

import com.SmartLaundry.model.DeliveryAgentAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface DeliveryAgentAvailabilityRepository extends JpaRepository<DeliveryAgentAvailability, String> {

    @Query("SELECT COUNT(a) > 0 FROM DeliveryAgentAvailability a " +
            "WHERE a.deliveryAgent.deliveryAgentId = :deliveryAgentId " +
            "AND a.date = :date " +
            "AND a.holiday = false " +
            "AND :time BETWEEN a.startTime AND a.endTime")
    boolean isAgentAvailable(@Param("agentId") String deliveryAgentId,
                             @Param("date") LocalDate date,
                             @Param("time") LocalTime time);

}
