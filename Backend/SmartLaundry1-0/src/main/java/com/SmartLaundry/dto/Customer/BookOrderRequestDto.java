package com.SmartLaundry.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookOrderRequestDto {
    private String serviceProviderId;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private List<OrderItemRequest> items;
    private boolean goWithSchedulePlan;

    public boolean isPayEachDelivery() {
        return true;
    }

    public boolean isPayLastDelivery() {
        return true;
    }

}

