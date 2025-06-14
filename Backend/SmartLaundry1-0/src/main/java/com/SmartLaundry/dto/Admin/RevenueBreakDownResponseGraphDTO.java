package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueBreakDownResponseGraphDTO {
    private LocalDate date;
    private Double serviceProviderRevenue;
    private Double deliveryAgentRevenue;
    private Double adminRevenue;
}
