package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintsSummaryResponseDTO {

    private Long totalComplaints;
    private Long customerComplaints;
    private Long deliveryAgentComplaints;
    private Long serviceProviderComplaints;
    private Long respondedComplaints;
    private Long nonRespondedComplaints;

}
