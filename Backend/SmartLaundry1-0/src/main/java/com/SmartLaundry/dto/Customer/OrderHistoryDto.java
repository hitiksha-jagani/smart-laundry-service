package com.SmartLaundry.dto.Customer;

import com.SmartLaundry.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class OrderHistoryDto {
    private String orderId;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private LocalDate deliveryDate;
    private LocalTime deliveryTime;
    private OrderStatus status;
    private String contactName;
    private String contactPhone;
    private String contactAddress;
    private LocalDateTime createdAt;
}
