package com.SmartLaundry.repository;

import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.BillStatus;
import com.SmartLaundry.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository  extends JpaRepository<Bill, String> {
    List<Bill> findAllByOrderByInvoiceNumberAsc(); //INV00001
    Bill findByOrder(Order order);
    Optional<Bill> findById(String invoiceNumber);
    //List<Bill> findByFinalPriceAndStatusAndUsers_UserId(Double finalPrice, BillStatus status, String userId);

}
