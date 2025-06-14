package com.SmartLaundry.repository;

import com.SmartLaundry.model.AdminRevenue;
import com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueRepository extends JpaRepository<AdminRevenue, Long> {

    @Query("SELECT COALESCE(SUM(r.totalRevenue), 0.0) FROM AdminRevenue r WHERE r.createdAt BETWEEN :start AND :end")
    Double findTotalRevenueByDateRange(@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(r.totalRevenue), 0.0) FROM AdminRevenue r")
    Double findTotalRevenue();

    @Query("SELECT COALESCE(COUNT(o), 0) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long findTotalOrderByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(COUNT(o), 0) FROM Order o")
    Long findTotalOrder();

    @Query("SELECT COALESCE(SUM(b.finalPrice), 0.0) FROM Bill b WHERE b.order.createdAt BETWEEN :start AND :end")
    Double findGrossSalesByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(b.finalPrice), 0.0) FROM Bill b")
    Double findGrossSales();

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.role = :role AND p.createdAt BETWEEN :start AND :end")
    Double findPayoutsByDateRangeAndRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") String role);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.role = :role")
    Double findPayoutsByRole(@Param("role") String role);

    @Query("SELECT COALESCE(AVG(b.finalPrice), 0.0) FROM Bill b WHERE b.order.createdAt BETWEEN :start AND :end")
    Double findAverageOrderValueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(AVG(b.finalPrice), 0.0) FROM Bill b")
    Double findAverageOrderValue();

    @Query("SELECT COALESCE(SUM(a.profitFromServiceProvider), 0.0) FROM AdminRevenue a WHERE a.createdAt BETWEEN :start AND :end")
    Double findRevenueFromServiceProviderByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(a.profitFromServiceProvider), 0.0) FROM AdminRevenue a")
    Double findRevenueFromServiceProvider();

    @Query("SELECT COALESCE(SUM(a.profitFromDeliveryAgent), 0.0) FROM AdminRevenue a WHERE a.createdAt BETWEEN :start AND :end")
    Double findRevenueFromDeliveryAgentDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(a.profitFromDeliveryAgent), 0.0) FROM AdminRevenue a")
    Double findRevenueFromDeliveryAgent();

    @Query("SELECT NEW com.SmartLaundry.dto.Admin.RevenueBreakDownResponseGraphDTO(FUNCTION('DATE', a.createdAt), " +
            "SUM(a.profitFromServiceProvider), SUM(a.profitFromDeliveryAgent), SUM(a.profitFromServiceProvider + a.profitFromDeliveryAgent)) " +
            "FROM AdminRevenue a WHERE a.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', a.createdAt) ORDER BY FUNCTION('DATE', a.createdAt)")
    List<RevenueBreakDownResponseGraphDTO> getRevenueBreakdownGroupedByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    List<Order> findByIdAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o")
    List<Order> findById();

    AdminRevenue findByPayment(Payment payment);
}
