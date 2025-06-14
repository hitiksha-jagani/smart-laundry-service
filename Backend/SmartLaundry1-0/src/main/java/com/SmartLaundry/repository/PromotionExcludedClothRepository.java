package com.SmartLaundry.repository;
import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Promotion;
import com.SmartLaundry.model.PromotionExcludedCloth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionExcludedClothRepository extends JpaRepository<PromotionExcludedCloth, Long> {

    boolean existsByPromotionAndItem(Promotion promotion, Items item);
}

