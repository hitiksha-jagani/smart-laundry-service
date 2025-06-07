package com.SmartLaundry.dto.Customer;

import com.SmartLaundry.model.SchedulePlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePlanRequestDto {
    private SchedulePlan schedulePlan; // Enum
    private boolean payEachDelivery;
    private boolean payLastDelivery;
}

