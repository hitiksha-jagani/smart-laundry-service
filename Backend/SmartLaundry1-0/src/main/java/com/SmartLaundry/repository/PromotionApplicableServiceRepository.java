package com.SmartLaundry.repository;

import com.SmartLaundry.model.Promotion;
import com.SmartLaundry.model.PromotionApplicableService;
import com.SmartLaundry.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionApplicableServiceRepository extends JpaRepository<PromotionApplicableService, Long> {

    boolean existsByPromotionAndService(Promotion promotion, Services service);

    List<PromotionApplicableService> findByPromotion(Promotion promotion);
}
