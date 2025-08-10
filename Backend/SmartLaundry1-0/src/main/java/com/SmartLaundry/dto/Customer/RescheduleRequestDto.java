package com.SmartLaundry.dto.Customer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RescheduleRequestDto {
    private String orderId;
    private LocalDate date;
    private String slot;
}

