package com.SmartLaundry.dto.Customer;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RaiseTicketRequestDto {

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Description is required.")
    private String description;

    private String photo;

    @NotBlank(message = "Category is required.")
    private String category;

    private LocalDateTime submittedAt;
    private String status;

    private String response;
    private LocalDateTime respondedAt;
}

