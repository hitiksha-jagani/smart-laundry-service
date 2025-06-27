package com.SmartLaundry.repository;

import com.SmartLaundry.dto.Admin.InsightResponseDTO;
import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.Payment;
import com.SmartLaundry.model.PaymentStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByBill(Bill bill);
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.bill.invoiceNumber = :invoiceNumber AND p.status = :status")
    boolean existsByBillInvoiceNumberAndStatus(@Param("invoiceNumber") String invoiceNumber,
                                               @Param("status") PaymentStatus status);


}
