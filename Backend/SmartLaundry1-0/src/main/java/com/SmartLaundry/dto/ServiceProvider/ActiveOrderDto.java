package com.SmartLaundry.dto.ServiceProvider;

import com.SmartLaundry.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveOrderDto {
    private String orderId;
    private String service;
    private String subService;
    private String itemName;
    private int quantity;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private OrderStatus status;
}

