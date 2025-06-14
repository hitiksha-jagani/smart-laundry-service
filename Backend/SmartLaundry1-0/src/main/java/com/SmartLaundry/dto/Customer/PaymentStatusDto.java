package com.SmartLaundry.dto.Customer;

import com.SmartLaundry.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentStatusDto {
    private String invoiceNumber;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime dateTime;
}

