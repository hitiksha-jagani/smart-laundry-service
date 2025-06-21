package com.SmartLaundry.repository;

import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.BillStatus;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository  extends JpaRepository<Bill, String> {
    List<Bill> findAllByOrderByInvoiceNumberAsc(); //INV00001
    Bill findByOrderAndStatus(@Param("order") Order order, @Param("status") BillStatus status);

    Optional<Bill> findById(String invoiceNumber);

    Bill findByOrder(Order order);

    @Query("""
    SELECT SUM(b.finalPrice)
    FROM Bill b
    WHERE b.order.createdAt BETWEEN :start AND :end""")
    Double getGrossSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


}
