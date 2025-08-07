package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.DeliveryAgentRevenueTableDTO;
import com.SmartLaundry.dto.Admin.InsightResponseDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderInsightDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRevenueTableDTO;
import com.SmartLaundry.model.Payment;
import com.SmartLaundry.model.Payout;
import com.SmartLaundry.model.UserRole;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, String> {
    List<Payout> findAllByOrderByPayoutIdAsc(); //PYT00001

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.createdAt BETWEEN :start AND :end")
    Double findTotalEarningsByUserIdAndDateRange(
            @Param("id") String id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id")
    Double findTotalEarningsByUserId(@Param("id") String id);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.status = 'PAID' AND p.createdAt BETWEEN :start AND :end")
    Double findPaidPayoutsByUserIdAndDateRange(
            @Param("id") String id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.status = 'PAID'")
    Double findPaidPayoutsByUserId(@Param("id") String id);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.status = 'PENDING' AND p.createdAt BETWEEN :start AND :end")
    Double findPendingPayoutsByUserIdAndDateRange(
            @Param("id") String id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.status = 'PENDING'")
    Double findPendingPayoutsByUserId(@Param("id") String id);

    @Query("SELECT p FROM Payout p WHERE p.createdAt BETWEEN :start AND :end")
    List<Payout> findPayoutsByUserIdAndDateRange(@Param("id") String id,@Param("start") LocalDateTime start,@Param("end")  LocalDateTime end);

    @Query("SELECT p FROM Payout p WHERE p.users.userId = :id")
    List<Payout> findTotalPayoutsByUserId(@Param("id") String id);

    @Query("SELECT p FROM Payout p WHERE p.createdAt BETWEEN :start AND :end AND p.status = 'PAID'")
    List<Payout> findPaidPayoutsByUserIdAndDateRangeAndStatus(@Param("id") String id,@Param("start") LocalDateTime start,@Param("end")  LocalDateTime end);

    @Query("SELECT p FROM Payout p WHERE p.users.userId = :id AND p.status = 'PAID'")
    List<Payout> findPaidPayoutsByUserIdAndStatus(@Param("id") String id);

    @Query("SELECT p FROM Payout p WHERE p.createdAt BETWEEN :start AND :end AND p.status = 'PENDING'")
    List<Payout> findPendingPayoutsByUserIdAndDateRangeAndStatus(@Param("id") String id,@Param("start") LocalDateTime start,@Param("end")  LocalDateTime end);

    @Query("SELECT p FROM Payout p WHERE p.users.userId = :id AND p.status = 'PENDING'")
    List<Payout> findPendingPayoutsByUserIdAndStatus(@Param("id") String id);

    List<Payout> findByPayment(Payment payment);

    @Query("SELECT SUM(p.bill.finalPrice) FROM Payment p WHERE p.dateTime BETWEEN :start AND :end AND p.status = 'PAID'")
    BigDecimal sumPaidPaymentsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM Payout p WHERE p.users.id = :providerId AND p.dateTime BETWEEN :start AND :end")
    List<Payout> findByServiceProviderAndDateRange(@Param("providerId") String providerId,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    @Query(value = """
    SELECT sp.business_name AS businessName, SUM(p.final_amount) AS totalRevenue
    FROM payout p
    JOIN users u ON p.user_id = u.user_id
    JOIN service_provider sp ON sp.user_id = u.user_id
    WHERE p.date_time BETWEEN :start AND :end
    GROUP BY sp.business_name
    ORDER BY totalRevenue DESC""", nativeQuery = true)
    List<ServiceProviderInsightDTO> findTopServiceProviderByPayoutInRangeNative(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new com.SmartLaundry.dto.Admin.ServiceProviderRevenueTableDTO(" +
            "sp.serviceProviderId, " +
            "SUM(p.finalAmount), " +
            "SUM(p.charge), " +
            "MAX(p.dateTime), " +
            "COUNT(p)) " +
            "FROM Payout p " +
            "JOIN p.users u " +
            "JOIN ServiceProvider sp ON sp.user = u " +
            "WHERE p.createdAt BETWEEN :start AND :end " +
            "AND u.role = :role " +
            "GROUP BY sp.serviceProviderId " +
            "ORDER BY SUM(p.finalAmount) DESC")
    List<ServiceProviderRevenueTableDTO> findAllProviderRevenuesInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("role") UserRole role
    );


    @Query("SELECT SUM(p.finalAmount) FROM Payout p WHERE p.users.userId = :userId AND p.createdAt BETWEEN :start AND :end")
    Double getRevenueForUserInRange(@Param("userId") String userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT new com.SmartLaundry.dto.Admin.DeliveryAgentRevenueTableDTO(" +
            "da.deliveryAgentId, " +
            "SUM(p.finalAmount), " +
            "SUM(p.charge), " +
            "MAX(p.dateTime), " +
            "COUNT(p)) " +
            "FROM Payout p " +
            "JOIN p.users u " +
            "JOIN DeliveryAgent da ON da.users = u " +
            "WHERE p.createdAt BETWEEN :start AND :end " +
            "GROUP BY da.deliveryAgentId " +
            "ORDER BY SUM(p.finalAmount) DESC")
    List<DeliveryAgentRevenueTableDTO> findAllAgentRevenuesInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT SUM(p.finalAmount)
    FROM Payout p
    WHERE p.users.role = com.SmartLaundry.model.UserRole.SERVICE_PROVIDER
      AND p.createdAt BETWEEN :start AND :end""")
    Double getProviderPayoutBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT SUM(p.finalAmount)
    FROM Payout p
    WHERE p.users.role = com.SmartLaundry.model.UserRole.DELIVERY_AGENT
      AND p.createdAt BETWEEN :start AND :end""")
    Double getDeliveryPayoutBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT SUM(p.finalAmount) 
    FROM Payout p
    WHERE p.createdAt BETWEEN :start AND :end""")
    Double getTotalPayoutBetween(LocalDateTime start, LocalDateTime end);

}
