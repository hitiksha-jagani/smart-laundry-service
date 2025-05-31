package com.SmartLaundry.repository;

import com.SmartLaundry.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository  extends JpaRepository<Bill, String> {
    List<Bill> findAllByOrderByInvoiceNumberAsc(); //INV00001{
}
