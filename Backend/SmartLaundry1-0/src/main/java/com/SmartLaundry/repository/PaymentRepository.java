package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.InsightResponseDTO;
import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.Payment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByBill(Bill bill);

//    @Query(value = """
//    SELECT COALESCE(SUM(b.delivery_charge), 0)
//    FROM payment p
//    JOIN bill b ON p.invoice_number = b.invoice_number
//    JOIN orders o ON b.order_id = o.order_id
//    WHERE p.status = 'PAID'
//      AND p.date_time BETWEEN :start AND :end
//    """, nativeQuery = true)
//    BigDecimal sumDeliveryPayoutsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
//
//    @Query(value = """
//    SELECT COALESCE(SUM(b.items_total_price), 0)
//    FROM payment p
//    JOIN bill b ON p.invoice_number = b.invoice_number
//    JOIN orders o ON b.order_id = o.order_id
//    WHERE p.status = 'PAID'
//      AND p.date_time BETWEEN :start AND :end
//    """, nativeQuery = true)
//    BigDecimal sumServiceProviderPayoutsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
