package com.SmartLaundry.dto.Admin;

import com.ctc.wstx.io.WstxInputData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueResponseDTO {
    private String revenueId;
}
