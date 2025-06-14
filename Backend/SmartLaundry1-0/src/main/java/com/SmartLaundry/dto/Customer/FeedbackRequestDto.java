package com.SmartLaundry.dto.Customer;

import lombok.Data;

@Data
public class FeedbackRequestDto {
    private String serviceProviderId;
    private Integer rating;
    private String review;
    private String orderId;
}

