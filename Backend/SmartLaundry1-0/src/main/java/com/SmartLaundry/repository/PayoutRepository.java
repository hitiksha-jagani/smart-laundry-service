package com.SmartLaundry.repository;

import com.SmartLaundry.model.Payment;
import com.SmartLaundry.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.status = 'PENDING' AND p.createdAt BETWEEN :start AND :end")
    Double findPendingPayoutsByUserIdAndDateRange(
            @Param("id") String id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payout p WHERE p.users.userId = :id AND p.status = 'PENDING'")
    Double findPendingPayoutsByUserId(@Param("id") String id);

    @Query("SELECT p FROM Payout p WHERE p.createdAt BETWEEN :start AND :end")
    List<Payout> findPayoutsByUserIdAndDateRange(String id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Payout p WHERE p.createdAt BETWEEN :start AND :end")
    List<Payout> findPayoutsByUserIdAndDateRangeAndStatus(String id, LocalDateTime start, LocalDateTime end);

    List<Payout> findByPayment(Payment payment);
}
