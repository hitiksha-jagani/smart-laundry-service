package com.SmartLaundry.dto.Customer;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {

    private String orderId;
    private String userId;
    private String serviceProviderId;

    private String contactName;
    private String contactPhone;
    private String contactAddress;
    private Double latitude;
    private Double longitude;

    private LocalDate pickupDate;
    private LocalTime pickupTime;

    private OrderStatus status;

    private List<BookingItemDto> bookingItems;

    private SchedulePlanDto schedulePlan; // Optional: null if not used



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BookingItemDto {
        private String itemId;
        private String itemName;
        private Integer quantity;
        private Double finalPrice;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SchedulePlanDto {
        private String plan; // Enum name
        private boolean payEachDelivery;
        private boolean payLastDelivery;
    }
}

