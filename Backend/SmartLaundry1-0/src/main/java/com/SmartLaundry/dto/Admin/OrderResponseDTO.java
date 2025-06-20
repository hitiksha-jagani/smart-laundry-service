package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.BookingItem;
import com.SmartLaundry.model.PayoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private String customerName;
    private String providerName;
    private String pickupAgentName;
    private String deliveryAgentName;
    private List<OrderResponseDTO.BookingItemDTO> bookingItemDTOS;
    private OrderResponseDTO.BillDTO billDTO;
    private OrderResponseDTO.PaymentDTO paymentDTO;
    private List<OrderResponseDTO.PayoutDTO> payoutDTOS;
    private AdminRevenueDTO adminRevenueDTO;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingItemDTO {
        private String itemId;
        private String itemName;
        private Integer quantity;
        private Double finalPrice;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BillDTO {
        private String invoiceNumber;
        private Double itemsTotalPrice;
        private Double deliveryCharge;
        private Double gstAmount;
        private Double discountAmount;
        private Double finalPrice;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDTO{
        private Long paymentId;
        private String transactionId;
        private LocalDateTime dateTime;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PayoutDTO{
        private String payoutId;
        private Double deliveryEarning;
        private Double charge;
        private Double finalAmount;
        private String transactionId;
        private PayoutStatus payoutStatus;
        private LocalDateTime dateTime;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminRevenueDTO{
        private Double profitFromDeliveryAgent;
        private Double profitFromServiceProvider;
        private Double totalRevenue;
    }
}
