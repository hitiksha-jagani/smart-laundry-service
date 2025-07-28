package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.PayoutStatus;
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
    private Double finalAmount;
    private LocalDateTime dateTime;
    private String orderId;
    private Double charge;
    private Double deliveryEarning;
    private PayoutStatus payoutStatus;
}
