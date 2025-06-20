package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAgentRevenueTableDTO {
    private String agentId;
    private double totalRevenue;
    private double platformCharges;
    private LocalDateTime dateTime;
    private Long payoutCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public DeliveryAgentRevenueTableDTO(String agentId, double totalRevenue, double platformCharges,
                                          LocalDateTime dateTime, Long payoutCount) {
        this.agentId = agentId;
        this.totalRevenue = totalRevenue;
        this.platformCharges = platformCharges;
        this.dateTime = dateTime;
        this.payoutCount = payoutCount;
    }

}
