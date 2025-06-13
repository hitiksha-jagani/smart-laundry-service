package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueSettingResponseDTO {
    private Double serviceProviderRevenue;
    private Double deliveryAgentRevenue;
    private String message;
}
