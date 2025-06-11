package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceSummaryDTO {
    private Long totalServices;
    private Long dryCleaningServices;
    private Long washServices;
    private Long washIronServices;
    private Long ironServices;
    private Long specializedServices;
}
