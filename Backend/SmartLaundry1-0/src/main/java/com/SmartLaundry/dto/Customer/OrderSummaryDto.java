package com.SmartLaundry.dto.Customer;
import com.SmartLaundry.model.BillStatus;
import com.SmartLaundry.model.OrderStatus;
import com.SmartLaundry.model.PaymentStatus;
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
    @Builder.Default
    private List<ItemSummary> items = List.of();
    @Builder.Default
    private double itemsTotal = 0.0;
    @Builder.Default
    private double gstAmount = 0.0;
    @Builder.Default
    private double deliveryCharge = 0.0;
    @Builder.Default
    private double discountAmount = 0.0;
    @Builder.Default
    private double finalAmount = 0.0;
    @Builder.Default
    private boolean isPromotionApplied = false;
    private String promotionMessage;
    private BillStatus status;
    private String appliedPromoCode;
    private String invoiceNumber;

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