package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.InsightResponseDTO;
import com.SmartLaundry.dto.Admin.TotalRevenueDTO;
import com.SmartLaundry.model.AdminRevenue;
import com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Payment;
import com.SmartLaundry.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueRepository extends JpaRepository<AdminRevenue, Long> {

    @Query("SELECT COALESCE(SUM(r.totalRevenue), 0.0) FROM AdminRevenue r WHERE r.createdAt BETWEEN :start AND :end")
    Double findTotalRevenueByDateRange(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(COUNT(o), 0) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long findTotalOrderByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(b.finalPrice), 0.0) FROM Bill b WHERE b.order.createdAt BETWEEN :start AND :end")
    Double findGrossSalesByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.role = :role AND p.createdAt BETWEEN :start AND :end")
    Double findPayoutsByDateRangeAndRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") UserRole role);

    @Query("SELECT COALESCE(AVG(b.finalPrice), 0.0) FROM Bill b WHERE b.order.createdAt BETWEEN :start AND :end")
    Double findAverageOrderValueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(a.profitFromServiceProvider), 0.0) FROM AdminRevenue a WHERE a.createdAt BETWEEN :start AND :end")
    Double findRevenueFromServiceProviderByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(a.profitFromDeliveryAgent), 0.0) FROM AdminRevenue a WHERE a.createdAt BETWEEN :start AND :end")
    Double findRevenueFromDeliveryAgentDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

//    @Query("SELECT NEW com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO(" +
//            "SUM(a.profitFromServiceProvider), " +
//            "SUM(a.profitFromDeliveryAgent) " +
//            "FROM AdminRevenue a WHERE a.createdAt BETWEEN :start AND :end " +
//            "GROUP BY FUNCTION('DATE', a.createdAt) ORDER BY FUNCTION('DATE', a.createdAt)")
//    List<RevenueBreakDownResponseGraphDTO> getRevenueBreakdownGroupedByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT NEW com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO(" +
            "SUM(a.profitFromServiceProvider), " +
            "SUM(a.profitFromDeliveryAgent)) " +  // removed the trailing comma
            "FROM AdminRevenue a " +
            "WHERE a.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', a.createdAt) " +
            "ORDER BY FUNCTION('DATE', a.createdAt)")
    RevenueBreakDownResponseGraphDTO getRevenueBreakdownGroupedByDate(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


    @Query(value = "SELECT * FROM orders WHERE created_at BETWEEN :start AND :end", nativeQuery = true)
    List<Order> findOrdersByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    AdminRevenue findByPayment(Payment payment);

//    @Query("""
//SELECT new com.SmartLaundry.dto.Admin.TotalRevenueDTO(
//    o.orderId,
//    ar.createdAt,
//    CONCAT(c.firstName, ' ', c.lastName),
//    b.finalPrice,
//    COALESCE(sp.finalAmount, 0.0),
//    COALESCE(da.finalAmount, 0.0),
//    ar.totalRevenue
//)
//FROM AdminRevenue ar
//JOIN ar.payment pay
//JOIN pay.bill b
//JOIN b.order o
//JOIN o.users c
//LEFT JOIN Payout sp ON sp.payment = pay AND sp.users.role = com.SmartLaundry.model.UserRole.SERVICE_PROVIDER
//LEFT JOIN Payout da ON da.payment = pay AND da.users.role = com.SmartLaundry.model.UserRole.DELIVERY_AGENT
//WHERE ar.createdAt BETWEEN :start AND :end
//GROUP BY o.orderId, ar.createdAt, c.firstName, c.lastName, b.finalPrice, ar.totalRevenue, sp.finalAmount, da.finalAmount
//ORDER BY ar.createdAt DESC
//""")
//    List<TotalRevenueDTO> getAdminRevenueBreakdownBetween(
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end
//    );

    @Query("""
    SELECT new com.SmartLaundry.dto.Admin.TotalRevenueDTO(
        o.orderId,
        ar.createdAt,
        CONCAT(c.firstName, ' ', c.lastName),
        b.finalPrice,

        MAX(CASE WHEN p.users.role = com.SmartLaundry.model.UserRole.SERVICE_PROVIDER THEN p.finalAmount ELSE 0 END),

        SUM(CASE WHEN p.users.role = com.SmartLaundry.model.UserRole.DELIVERY_AGENT THEN p.finalAmount ELSE 0 END),

        ar.totalRevenue
    )
    FROM AdminRevenue ar
    JOIN ar.payment pay
    JOIN pay.bill b
    JOIN b.order o
    JOIN o.users c
    LEFT JOIN Payout p ON p.payment = pay
    WHERE ar.createdAt BETWEEN :start AND :end
    GROUP BY o.orderId, ar.createdAt, c.firstName, c.lastName, b.finalPrice, ar.totalRevenue
    ORDER BY ar.createdAt DESC
""")
    List<TotalRevenueDTO> getAdminRevenueBreakdownBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );







}
