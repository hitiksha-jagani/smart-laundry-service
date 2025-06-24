package com.SmartLaundry.dto.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingDeliveriesResponseDTO {
    private String orderId;
    private Double deliveryEarning;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private String pickupName;
    private String pickupPhone;
    private String pickupAddress;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;
    private Long totalQuantity;
    private Double km;
    private String deliveryType; // Customer -> Service provider, Service provider -> Customer

    private List<BookingItemDTO> bookingItemDTOList;

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
