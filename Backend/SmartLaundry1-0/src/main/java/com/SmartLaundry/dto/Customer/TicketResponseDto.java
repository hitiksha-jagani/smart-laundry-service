package com.SmartLaundry.dto.Customer;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponseDto {
    private String responseText;
    private boolean makeFaqVisible = false; // default false
    private String status;
}

