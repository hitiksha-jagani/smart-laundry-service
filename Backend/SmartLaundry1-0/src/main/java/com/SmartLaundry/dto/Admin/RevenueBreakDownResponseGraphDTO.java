package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
public class RevenueBreakDownResponseGraphDTO {
    private Double serviceProviderRevenue;
    private Double deliveryAgentRevenue;

    public RevenueBreakDownResponseGraphDTO(Double serviceProviderRevenue, Double deliveryAgentRevenue) {
        this.serviceProviderRevenue = serviceProviderRevenue;
        this.deliveryAgentRevenue = deliveryAgentRevenue;
    }
}
