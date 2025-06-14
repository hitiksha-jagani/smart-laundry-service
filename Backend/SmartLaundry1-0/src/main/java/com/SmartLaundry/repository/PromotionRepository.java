package com.SmartLaundry.repository;

import com.SmartLaundry.model.Promotion;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, String> {

    List<Promotion> findAllByOrderByPromotionIdAsc();
    @Query("SELECT p FROM Promotion p WHERE TRIM(LOWER(p.promoCode)) = TRIM(LOWER(:promoCode)) AND p.isActive = true")
    Optional<Promotion> findActiveByPromoCode(@Param("promoCode") String promoCode);



    List<Promotion> findAllByIsActiveTrue();
}

