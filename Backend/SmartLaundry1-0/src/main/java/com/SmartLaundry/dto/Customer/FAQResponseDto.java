package com.SmartLaundry.dto.Customer;

import lombok.Data;

import java.io.Serializable;

@Data
public class FAQResponseDto implements Serializable {
    private Long faqId;
    private Long ticketId;
    private Boolean visibilityStatus;
    private String question;
    private String answer;
    private String category;
}
