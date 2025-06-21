package com.SmartLaundry.dto.Customer;

import com.SmartLaundry.model.SchedulePlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePlanRequestDto {

    private SchedulePlan schedulePlan; // Enum: DAILY / WEEKLY / MONTHLY
    private boolean payEachDelivery;
    private boolean payLastDelivery;

    public boolean isValidPaymentOption() {
        // Only one of them must be true
        return payEachDelivery ^ payLastDelivery; // XOR
    }

    public void validate() {
        if (schedulePlan == null) {
            throw new IllegalArgumentException("Schedule plan must be selected.");
        }
        if (!isValidPaymentOption()) {
            throw new IllegalArgumentException("Exactly one payment option must be selected: either 'Pay Each Delivery' or 'Pay Last Delivery'.");
        }
    }
}
