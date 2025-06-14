package com.SmartLaundry.dto.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingDeliveriesResponseDTO {
    private String orderId;
    private Double deliveryEarning;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String providerName;
    private String providerPhone;
    private String providerAddress;
    private Long totalQuantity;
    private Double km;
    private String deliveryType; // Customer -> Service provider, Service provider -> Customer

    private List<PendingDeliveriesResponseDTO.BookingItemDTO> bookingItemDTOList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingItemDTO{
        private String itemName;
        private String serviceName;
        private Integer quantity;
    }
}
