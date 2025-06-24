package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.DeliveryAgentInsightDTO;
import com.SmartLaundry.dto.Admin.InsightResponseDTO;
import com.SmartLaundry.dto.Admin.OrderInsightDTO;
import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.ServiceProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProviderAndStatus(ServiceProvider serviceProvider, OrderStatus status);

    // To list all orders sorted by OrderId in ascending order
    List<Order> findAllByOrderByOrderIdAsc();
    Optional<Order> findByorderId(String orderId);

    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProvider_ServiceProviderIdAndStatus(String serviceProviderId, OrderStatus status);
    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN OrderOtp otp ON otp.order = o
    WHERE o.serviceProvider.serviceProviderId = :providerId
      AND otp.isUsed = false
      AND (otp.purpose = 'PICKUP_CONFIRMATION' OR otp.purpose = 'DELIVERY_CONFIRMATION')
""")
    List<Order> findOrdersWithPendingOtpForProvider(@Param("providerId") String providerId);
    // In OrderRepository.java
    @Query("SELECT o FROM Order o WHERE o.serviceProvider = :provider AND o.status IN (com.SmartLaundry.model.OrderStatus.IN_CLEANING, com.SmartLaundry.model.OrderStatus.OUT_FOR_DELIVERY)")
    List<Order> findAllByServiceProviderAndOtpVerificationRequired(@Param("provider") ServiceProvider provider);

    List<Order> findByServiceProvider(ServiceProvider serviceProvider);
    List<Order> findByPickupDate(LocalDate pickupDate);

    List<Order> findByStatusAndPickupDeliveryAgent(@Param("orderStatus") OrderStatus orderStatus,@Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByPickupDeliveryAgent(@Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByDeliveryDeliveryAgent(@Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByStatusAndDeliveryDeliveryAgent(@Param("orderStatus") OrderStatus orderStatus, @Param("deliveryAgent") DeliveryAgent deliveryAgent);

    List<Order> findByStatusAndDeliveryDeliveryAgentAndDeliveryDate(@Param("orderStatus") OrderStatus orderStatus, @Param("deliveryAgent") DeliveryAgent deliveryAgent, @Param("now") LocalDate now);

    List<Order> findByStatusAndPickupDeliveryAgentAndPickupDate(@Param("orderStatus") OrderStatus orderStatus, @Param("deliveryAgent") DeliveryAgent deliveryAgent, @Param("now") LocalDate now);


    List<Order> findByServiceProvider_ServiceProviderId(String providerId);

    @Query("""
    SELECT new com.SmartLaundry.dto.Admin.OrderInsightDTO(o.orderId, b.finalPrice)
    FROM Bill b
    JOIN b.order o
    WHERE o.createdAt BETWEEN :start AND :end
    ORDER BY b.finalPrice DESC""")
    OrderInsightDTO findTopOrderByValueBetweenDates(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
    SELECT CONCAT(u.first_name, ' ', u.last_name) AS agentName, COUNT(*) AS deliveries
    FROM (
        SELECT o.pickup_delivery_agent_id AS agent_id
        FROM orders o
        WHERE o.pickup_delivery_agent_id IS NOT NULL
          AND o.created_at BETWEEN :start AND :end

        UNION ALL

        SELECT o.delivery_delivery_agent_id AS agent_id
        FROM orders o
        WHERE o.delivery_delivery_agent_id IS NOT NULL
          AND o.created_at BETWEEN :start AND :end
    ) AS combined
    JOIN delivery_agent da ON da.delivery_agent_id = combined.agent_id
    JOIN users u ON u.user_id = da.user_id
    GROUP BY u.first_name, u.last_name
    ORDER BY deliveries DESC
    """, nativeQuery = true)
    List<DeliveryAgentInsightDTO> findTopDeliveryAgentsInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);

    long countByServiceProviderAndCreatedAtBetween(ServiceProvider provider, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE " +
            "(o.pickupDeliveryAgent.deliveryAgentId = :agentId OR o.deliveryDeliveryAgent.deliveryAgentId = :agentId) " +
            "AND o.createdAt BETWEEN :start AND :end")
    long countByDeliveryDeliveryAgentOrPickupAgentBetween(String agentId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.serviceProvider.serviceProviderId = :id AND o.createdAt BETWEEN :start AND :end")
    long countByServiceProviderIdAndCreatedAtBetween(String id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE (o.pickupDeliveryAgent.deliveryAgentId = :id OR o.deliveryDeliveryAgent.deliveryAgentId = :id) AND o.createdAt BETWEEN :start AND :end")
    long countByAgentIdAndCreatedAtBetween(String id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.serviceProvider.serviceProviderId = :id AND o.status = :status AND o.createdAt BETWEEN :start AND :end")
    long countByServiceProviderIdAndStatusAndCreatedAtBetween(String id, OrderStatus status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE (o.pickupDeliveryAgent.deliveryAgentId = :id OR o.deliveryDeliveryAgent.deliveryAgentId = :id) AND o.status = :status AND o.createdAt BETWEEN :start AND :end")
    long countByAgentIdAndStatusAndCreatedAtBetween(String id, OrderStatus status, LocalDateTime start, LocalDateTime end);

}