package com.SmartLaundry.dto.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayoutResponseDTO {
    private String payoutId;
    private Double amount;
    private LocalDateTime dateTime;
    private String orderId;
}
