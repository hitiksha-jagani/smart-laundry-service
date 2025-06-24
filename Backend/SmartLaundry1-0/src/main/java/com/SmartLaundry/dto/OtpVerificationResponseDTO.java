package com.SmartLaundry.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpVerificationResponseDTO {
    private String orderId;
    private String customerName;
    private boolean requiresPickupOtp;
    private boolean requiresDeliveryOtp;
    private String agentId;
    private String providerId;
}

