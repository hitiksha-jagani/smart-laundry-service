package com.SmartLaundry.repository;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderOtp;
import com.SmartLaundry.model.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderOtpRepository extends JpaRepository<OrderOtp, Integer> {
    Optional<OrderOtp> findTopByOrderAndPurposeAndIsUsedFalseOrderByGeneratedAtDesc(Order order, OtpPurpose purpose);

}

