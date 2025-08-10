package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.model.CurrentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAgentEarningSettingRequestDTO {

    @DecimalMin(value = "0.0", inclusive = false, message = "Base km cannot be negative or zero.")
    private Double baseKm;

    @NotNull(message = "Fixed amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fixed amount cannot be negative or zero.")
    private Double fixedAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Extra per km amount cannot be negative or zero")
    private Double extraPerKmAmount;

    @NotNull(message = "Current status is required.")
    private CurrentStatus currentStatus;

}
