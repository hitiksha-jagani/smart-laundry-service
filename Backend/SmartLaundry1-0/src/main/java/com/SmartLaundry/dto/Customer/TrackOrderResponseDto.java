package com.SmartLaundry.dto.Customer;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class TrackOrderResponseDto {
    private String orderId;
    private String status;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private List<StatusHistoryDto> statusHistory;
}

