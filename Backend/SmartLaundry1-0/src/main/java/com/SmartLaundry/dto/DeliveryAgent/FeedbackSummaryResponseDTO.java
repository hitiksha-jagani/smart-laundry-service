package com.SmartLaundry.dto.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackSummaryResponseDTO {

    private Long totalReviews;
    private Double averageRating;
}
