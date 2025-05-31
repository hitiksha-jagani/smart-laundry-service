package com.SmartLaundry.repository;

import com.SmartLaundry.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
    List<Promotion> findAllByOrderByPromotionIdAsc(); //PR001{
}
