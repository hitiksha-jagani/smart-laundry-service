package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.DeliveryAgentInsightDTO;
import com.SmartLaundry.dto.Admin.InsightResponseDTO;
import com.SmartLaundry.dto.Admin.OrderInsightDTO;
import com.SmartLaundry.model.*;
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
    List<Order> findByUsers_UserId(String userId);
    List<Order> findByPickupDeliveryAgentAndPickupDate(DeliveryAgent deliveryAgent, LocalDate now);

    List<Order> findByDeliveryDeliveryAgentAndDeliveryDate(DeliveryAgent deliveryAgent, LocalDate now);
    // Corrected method to find orders by service provider ID and status
    List<Order> findByServiceProvider_ServiceProviderIdAndStatus(String serviceProviderId, OrderStatus status);
    @Query("""
    SELECT o FROM Order o
    WHERE o.serviceProvider = :provider
      AND (
        (o.status = 'ACCEPTED_BY_PROVIDER' AND EXISTS (
          SELECT otp FROM OrderOtp otp
          WHERE otp.order.orderId = o.orderId
            AND otp.purpose = 'PICKUP_CUSTOMER'
            AND otp.isUsed = false
        ))
        OR
        (o.status = 'PICKED_UP' AND EXISTS (
          SELECT otp FROM OrderOtp otp
          WHERE otp.order.orderId = o.orderId
            AND otp.purpose = 'HANDOVER_TO_PROVIDER'
            AND otp.isUsed = false
        ))
        OR
        (o.status = 'READY_FOR_DELIVERY' AND EXISTS (
          SELECT otp FROM OrderOtp otp
          WHERE otp.order.orderId = o.orderId
            AND (
              (otp.purpose = 'CONFIRM_FOR_CLOTHS' AND otp.isUsed = false)
              OR
              (otp.purpose = 'DELIVERY_CUSTOMER' AND otp.isUsed = false)
            )
        ))
      )
""")
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
    long countByDeliveryDeliveryAgentOrPickupAgentBetween(
            @Param("agentId") String agentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.serviceProvider.serviceProviderId = :id AND o.createdAt BETWEEN :start AND :end")
    long countByServiceProviderIdAndCreatedAtBetween(String id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE (o.pickupDeliveryAgent.deliveryAgentId = :id OR o.deliveryDeliveryAgent.deliveryAgentId = :id) AND o.createdAt BETWEEN :start AND :end")
    long countByAgentIdAndCreatedAtBetween(String id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.serviceProvider.serviceProviderId = :id AND o.status = :status AND o.createdAt BETWEEN :start AND :end")
    long countByServiceProviderIdAndStatusAndCreatedAtBetween(@Param("id") String id,
                                                              @Param("status") OrderStatus status,
                                                              @Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE (o.pickupDeliveryAgent.deliveryAgentId = :id OR o.deliveryDeliveryAgent.deliveryAgentId = :id) AND o.status = :status AND o.createdAt BETWEEN :start AND :end")
    long countByAgentIdAndStatusAndCreatedAtBetween(@Param("id") String id,
                                                    @Param("status") OrderStatus status,
                                                    @Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses AND o.pickupDeliveryAgent = :agent AND o.pickupDate = :date")
    List<Order> findCustomerToProviderOrders(@Param("statuses") List<OrderStatus> statuses,
                                             @Param("agent") DeliveryAgent agent,
                                             @Param("date") LocalDate date);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses AND o.deliveryDeliveryAgent = :agent AND o.deliveryDate = :date")
    List<Order> findProviderToCustomerOrders(@Param("statuses") List<OrderStatus> statuses,
                                             @Param("agent") DeliveryAgent agent,
                                             @Param("date") LocalDate date);

    List<Order> findByPickupDeliveryAgentAndPickupDate(DeliveryAgent deliveryAgent, LocalDate now);

    List<Order> findByDeliveryDeliveryAgentAndDeliveryDate(DeliveryAgent deliveryAgent, LocalDate now);
}