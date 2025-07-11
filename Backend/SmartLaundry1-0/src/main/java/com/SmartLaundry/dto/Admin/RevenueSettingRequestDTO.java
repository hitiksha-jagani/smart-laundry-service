package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.model.CurrentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CurrentStatus currentStatus;
}
