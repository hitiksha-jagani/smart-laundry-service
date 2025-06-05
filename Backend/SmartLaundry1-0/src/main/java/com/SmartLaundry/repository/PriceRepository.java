package com.SmartLaundry.repository;

import com.SmartLaundry.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
