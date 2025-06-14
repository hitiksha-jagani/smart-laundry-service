package com.SmartLaundry.service.Customer;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.PromotionApplicableServiceRepository;
import com.SmartLaundry.repository.PromotionExcludedClothRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PromotionEvaluatorService {

    private final PromotionApplicableServiceRepository applicableServiceRepo;
    private final PromotionExcludedClothRepository excludedClothRepo;

    public String getPromotionValidationMessage(Promotion promotion, List<BookingItem> orderItems, BigDecimal amount, LocalDateTime createdAt) {
        if (!Boolean.TRUE.equals(promotion.getIsActive())) {
            return "Promotion is currently not active.";
        }

        if (createdAt.isBefore(promotion.getStartDate().atStartOfDay()) ||
                createdAt.isAfter(promotion.getEndDate().atTime(LocalTime.MAX))) {
            return "Promotion is not valid at the time of order.";
        }

        if (amount.compareTo(BigDecimal.valueOf(promotion.getMinOrderAmount())) < 0) {
            return "Promotion applicable only for orders of amount at least " + promotion.getMinOrderAmount();
        }

        Set<Services> servicesInOrder = orderItems.stream()
                .map(item -> {
                    Items itemEntity = item.getItem();
                    SubService subService = itemEntity.getSubService();
                    return subService != null ? subService.getServices() : itemEntity.getService();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Items> itemsInOrder = orderItems.stream()
                .map(BookingItem::getItem)
                .collect(Collectors.toSet());

        for (Items item : itemsInOrder) {
            if (excludedClothRepo.existsByPromotionAndItem(promotion, item)) {
                return "Promotion not applicable: Item '" + item.getItemName() + "' is excluded.";
            }
        }

        List<PromotionApplicableService> requiredServices = applicableServiceRepo.findByPromotion(promotion);
        for (PromotionApplicableService required : requiredServices) {
            if (!servicesInOrder.contains(required.getService())) {
                return "Promotion requires service '" + required.getService().getServiceName() + "'";
            }
        }

        return null; // Promotion is valid
    }

    public BigDecimal calculateDiscount(Promotion promotion, BigDecimal amount) {
        BigDecimal discount;

        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            // Calculate percentage-based discount
            discount = amount.multiply(BigDecimal.valueOf(promotion.getDiscountValue()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Fixed amount discount
            discount = BigDecimal.valueOf(promotion.getDiscountValue());
        }

        // Apply maxDiscount cap (if applicable)
        if (promotion.getMaxDiscount() != null) {
            BigDecimal maxDiscount = BigDecimal.valueOf(promotion.getMaxDiscount());
            discount = discount.min(maxDiscount);
        }

        return discount;
    }

}
