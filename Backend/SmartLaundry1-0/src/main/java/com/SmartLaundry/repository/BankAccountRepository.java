package com.SmartLaundry.repository;

import com.SmartLaundry.model.BankAccount;
import com.SmartLaundry.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
