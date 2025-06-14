package com.SmartLaundry.repository;

import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository  extends JpaRepository<Bill, String> {
    List<Bill> findAllByOrderByInvoiceNumberAsc(); //INV00001{

    Bill findByOrder(Order order);
}
