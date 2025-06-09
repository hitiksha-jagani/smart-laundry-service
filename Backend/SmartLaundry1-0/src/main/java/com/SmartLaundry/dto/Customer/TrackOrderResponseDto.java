package com.SmartLaundry.dto.Customer;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TrackOrderResponseDto {
    private String orderId;
    private String status;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
}

