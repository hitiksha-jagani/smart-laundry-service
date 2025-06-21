package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProviderRevenueTableDTO {
    private String providerId;
    private double totalRevenue;
    private double platformCharges;
    private LocalDateTime dateTime;
    private Long payoutCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ServiceProviderRevenueTableDTO(String providerId, double totalRevenue, double platformCharges,
                                          LocalDateTime dateTime, Long payoutCount) {
        this.providerId = providerId;
        this.totalRevenue = totalRevenue;
        this.platformCharges = platformCharges;
        this.dateTime = dateTime;
        this.payoutCount = payoutCount;
    }

}
