package com.SmartLaundry.service.Customer;
import com.SmartLaundry.model.Promotion;
import com.SmartLaundry.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public Promotion getPromotionById(String promotionId) {
        System.out.println("Fetching promotion with ID: " + promotionId);

        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionId));
    }


    public List<Promotion> getAvailablePromotionsForOrder(LocalDateTime orderCreatedAt) {
        return promotionRepository.findAll().stream()
                .filter(promotion ->
                        orderCreatedAt.isAfter(promotion.getStartDate().atStartOfDay()) &&
                                orderCreatedAt.isBefore(promotion.getEndDate().atTime(LocalTime.MAX))
                )
                .collect(Collectors.toList());
    }


}

