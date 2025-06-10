package com.SmartLaundry.dto.Customer;

import lombok.Data;

import java.io.Serializable;

@Data
public class FAQRequestDto {
    private Long ticketId; // nullable, optional
    private Boolean visibilityStatus;
    private String question;
    private String answer;
    private String category;
}

