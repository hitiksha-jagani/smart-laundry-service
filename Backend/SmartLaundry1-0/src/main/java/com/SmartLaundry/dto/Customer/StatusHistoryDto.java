package com.SmartLaundry.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistoryDto {
    private String status;
    private LocalDateTime changedAt;
}
