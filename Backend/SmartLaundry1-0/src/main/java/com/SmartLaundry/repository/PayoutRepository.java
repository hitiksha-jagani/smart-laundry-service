package com.SmartLaundry.repository;

import com.SmartLaundry.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, String> {
    List<Payout> findAllByOrderByPayoutIdAsc(); //PYT00001
}
