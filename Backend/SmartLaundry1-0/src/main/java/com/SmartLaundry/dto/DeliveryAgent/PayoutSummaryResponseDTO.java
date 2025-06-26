package com.SmartLaundry.dto.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutSummaryResponseDTO {
    private Double totalEarnings;
    private Double paidPayouts;
    private Double pendingPayouts;
}
