package com.SmartLaundry.repository;

import com.SmartLaundry.model.RejectedOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RejectedOrdersRepository extends JpaRepository<RejectedOrders, Long> {

    @Query("""
        SELECT COUNT(DISTINCT ro.order.orderId)
        FROM RejectedOrders ro
        WHERE ro.order.createdAt BETWEEN :start AND :end
    """)
    long countDistinctOrders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT COUNT(DISTINCT ro.order.orderId) FROM RejectedOrders ro " +
            "WHERE ro.order.serviceProvider.serviceProviderId = :providerId " +
            "AND ro.order.createdAt BETWEEN :start AND :end")
    long countDistinctOrdersByServiceProvider(String providerId, LocalDateTime start, LocalDateTime end);


//    @Query("SELECT COUNT(DISTINCT ro.order.orderId) FROM RejectedOrders ro " +
//            "WHERE (ro.order.pickupDeliveryAgent.deliveryAgentId = :agentId " +
//            "OR ro.order.deliveryDeliveryAgent.deliveryAgentId = :agentId) " +
//            "AND ro.order.createdAt BETWEEN :start AND :end")
//    long countDistinctOrdersByAgent(@Param("agentId") String agentId,
//                                    @Param("start") LocalDateTime start,
//                                    @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT ro.order.orderId) FROM RejectedOrders ro WHERE ro.users.userId = :userId AND ro.order.createdAt BETWEEN :start AND :end")
    long countDistinctOrdersByAgent(@Param("userId") String userId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}
