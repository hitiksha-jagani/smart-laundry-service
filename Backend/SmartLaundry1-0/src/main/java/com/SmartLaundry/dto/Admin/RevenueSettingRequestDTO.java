package com.SmartLaundry.dto.Admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueSettingRequestDTO {

    @NotNull(message = "Service provider revenue part is required.")
    private Double serviceProviderRevenue;

    @NotNull(message = "Delivery agent revenue part is required.")
    private Double deliveryAgentRevenue;
}
