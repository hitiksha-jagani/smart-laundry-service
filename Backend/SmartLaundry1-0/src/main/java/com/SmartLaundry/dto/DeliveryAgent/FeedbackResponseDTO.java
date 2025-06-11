package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDTO {
    private String customerName;
    private Integer rating;
    private String review;
    private String orderId;
    private LocalDateTime createdAt;
}
