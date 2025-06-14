package com.SmartLaundry.repository;

import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByBill(Bill bill);
}
