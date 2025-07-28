package com.SmartLaundry.dto.Admin;

import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalRevenueDTO {
    private String orderId;
    private LocalDateTime date;
    private String customerName;
    private Double totalPaid;
    private Double providerPayout;
    private Double agentPayout;
    private Double adminRevenue;
}
