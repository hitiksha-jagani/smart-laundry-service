package com.SmartLaundry.dto.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySummaryResponseDTO {
    private Integer totalDeliveries;
    private Integer pendingDeliveries;
    private Integer todayDeliveries;
}
