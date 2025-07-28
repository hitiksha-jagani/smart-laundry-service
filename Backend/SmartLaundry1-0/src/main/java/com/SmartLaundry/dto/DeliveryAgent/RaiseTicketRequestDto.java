//package com.SmartLaundry.dto.DeliveryAgent;
//import com.SmartLaundry.model.TicketStatus;
//import jakarta.validation.constraints.NotBlank;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class RaiseTicketRequestDto {
//
//    private Long ticketId;
//
//    @NotBlank(message = "Title is required.")
//    private String title;
//
//    @NotBlank(message = "Description is required.")
//    private String description;
//
//    private String photo;
//
//    @NotBlank(message = "Category is required.")
//    private String category;
//
//    private LocalDateTime submittedAt;
//    private TicketStatus status;
//
//    private String response;
//    private LocalDateTime respondedAt;
//}
//
