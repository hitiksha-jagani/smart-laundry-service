package com.SmartLaundry.dto.Customer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RaiseTicketRequestDto {
    private String title;
    private String description;
    private String photo;
    private String category;
    private LocalDateTime submittedAt;
    private String status;
}

