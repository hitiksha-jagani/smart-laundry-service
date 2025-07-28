package com.SmartLaundry.dto;

import lombok.Data;

@Data
public class RazorpaySuccessDTO {
    private String paymentId;
    private String invoiceNumber;
    private String method;
}

