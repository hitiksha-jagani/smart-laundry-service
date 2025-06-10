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

    /**
     * Returns true if exactly one payment option is selected.
     * Change this logic if both false is valid.
     */
    public boolean isValidPaymentOption() {
        return payEachDelivery ^ payLastDelivery; // XOR: true if exactly one is true
    }

    /**
     * Validates the DTO and throws IllegalArgumentException if invalid.
     */
    public void validate() {
        if (!isValidPaymentOption()) {
            throw new IllegalArgumentException("Exactly one payment option must be selected: payEachDelivery or payLastDelivery.");
        }
    }
}


