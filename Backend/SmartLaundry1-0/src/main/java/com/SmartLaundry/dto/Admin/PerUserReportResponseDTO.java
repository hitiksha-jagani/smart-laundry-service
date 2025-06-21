package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerUserReportResponseDTO {
    private String userId;
    private String id; // serviceProviderId or deliveryAgentId
    private String businessName;
    private long orderCount;
    private long rejectedOrderCount;
}
