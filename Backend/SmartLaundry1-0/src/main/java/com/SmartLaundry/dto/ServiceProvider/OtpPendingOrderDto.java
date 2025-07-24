package com.SmartLaundry.dto.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpPendingOrderDto {
    private String orderId;
    private String agentId;              // pickup or delivery agent
    private String providerId;
    private boolean requiresPickupOtp;
    private boolean requiresDeliveryOtp;
    private String customerName;         // ✅ Added for frontend display
}
