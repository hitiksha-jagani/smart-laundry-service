package com.SmartLaundry.repository;

import com.SmartLaundry.model.AdminRevenue;
import com.SmartLaundry.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AdminRevenueRepository extends JpaRepository<AdminRevenue, Long> {
    AdminRevenue findByPayment(Payment payment);

    @Query("""
    SELECT SUM(ar.totalRevenue)
    FROM AdminRevenue ar
    WHERE ar.createdAt BETWEEN :start AND :end""")
    Double getAdminRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
