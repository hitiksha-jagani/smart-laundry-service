package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightResponseDTO {
    private String businessName;
    private Double sales;
    private String agentName;
    private Long deliveries;
    private String orderId;
    private Double orderValue;
}
