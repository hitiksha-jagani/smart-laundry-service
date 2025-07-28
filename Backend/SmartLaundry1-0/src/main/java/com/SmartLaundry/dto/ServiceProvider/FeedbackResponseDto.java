package com.SmartLaundry.dto.ServiceProvider;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponseDto {
    private String customerName;
    private String review;
    private int rating;
    private LocalDateTime submittedAt;
}

