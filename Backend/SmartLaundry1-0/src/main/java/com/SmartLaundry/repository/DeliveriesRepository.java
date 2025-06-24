package com.SmartLaundry.repository;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DeliveriesRepository extends JpaRepository<DeliveryAgent, String> {
//    @Query(value = "SELECT COUNT(*) FROM orders WHERE delivery_agent_id = :agentId", nativeQuery = true)
//    Integer countDeliveriesByAgent(@Param("agentId") String agentId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'ACCEPTED_BY_AGENT' AND o.pickupDate = :today AND o.pickupDeliveryAgent.deliveryAgentId = :agentId")
    Integer countAcceptedPickupOrdersForToday(@Param("today") LocalDate today,@Param("agentId") String agentId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'READY_FOR_DELIVERY' AND o.pickupDate = :today AND o.deliveryDeliveryAgent.deliveryAgentId = :agentId")
    Integer countAcceptedDeliveryOrdersForToday(@Param("today") LocalDate today,@Param("agentId")  String agentId);
}
