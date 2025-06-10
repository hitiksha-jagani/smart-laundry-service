package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.OrderStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAssignmentDTO {
    private String agentId;
    private OrderStatus status;
    private long assignedAt;
}

