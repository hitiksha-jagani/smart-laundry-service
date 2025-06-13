package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.model.TicketStatus;
import com.SmartLaundry.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintsListResponseDTO {
    public Long complaintId;
    private String title;
    private String description;
    private String photo;
    private String category;
    private String response;
    private TicketStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime respondedAt;
    private UserRole userType;
    private String userName;
}
