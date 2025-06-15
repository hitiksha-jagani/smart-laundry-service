package com.SmartLaundry.dto.Customer;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private String orderId;
    private String userId;
    private String serviceProviderId;

    private String contactName;
    private String contactPhone;
    private String contactAddress;

    @Builder.Default
    private double latitude = 0.0;

    @Builder.Default
    private double longitude = 0.0;

    private LocalDate pickupDate;
    private LocalTime pickupTime;

    private OrderStatus status;

    @Builder.Default
    private List<BookingItemDto> bookingItems = new ArrayList<>();

    private SchedulePlanDto schedulePlan;

    @Builder.Default
    private boolean needOfDeliveryAgent = false;

    @Data
    @Builder
    public static class BookingItemDto {
        private String itemId;
        private String itemName;
        private Integer quantity;
        private Double finalPrice;
    }

    @Data
    @Builder
    public static class SchedulePlanDto {
        private String plan;
        private boolean payEachDelivery;
        private boolean payLastDelivery;
    }
}