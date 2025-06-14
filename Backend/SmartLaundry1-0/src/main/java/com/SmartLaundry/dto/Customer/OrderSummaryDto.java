package com.SmartLaundry.dto.Customer;
import com.SmartLaundry.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSummaryDto {
    private String orderId;
    private String serviceName;
    private String subServiceName;
    private List<ItemSummary> items;
    private double itemsTotal;
    private double gstAmount;
    private double deliveryCharge;
    private double discountAmount;
    private double finalAmount;
    private boolean isPromotionApplied;
    private String promotionMessage;
    private OrderStatus orderStatus;
    private String appliedPromoCode;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemSummary {
        private String itemName;
        private int quantity;
        private double price;
        private double finalPrice;
    }
}

