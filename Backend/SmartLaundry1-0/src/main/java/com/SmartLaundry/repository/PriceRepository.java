package com.SmartLaundry.repository;


import com.SmartLaundry.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    // Optional: Add custom queries if needed later
}

