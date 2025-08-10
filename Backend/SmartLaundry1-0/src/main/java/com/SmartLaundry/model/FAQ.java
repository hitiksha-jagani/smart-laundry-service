package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "faq")
@Schema(description = "Frequently Asked Questions related to support tickets.")
public class FAQ implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "faq_id", nullable = false, updatable = false)
    @Schema(example = "1")
    private Long faqId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    @Schema(description = "Reference to support ticket ID", example = "TKT123")
    private Ticket ticket;

    @NotNull
    @Column(name = "visibility_status", nullable = false)
    @Schema(description = "Whether FAQ is visible to users", example = "true")
    private Boolean visibilityStatus;

    @NotBlank
    @Column(name = "question", nullable = false)
    @Schema(description = "The FAQ question", example = "How can I cancel my order?")
    private String question;

    @NotBlank
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Answer to the FAQ question", example = "Go to 'My Orders' and click 'Cancel Order'.")
    private String answer;

    @Column(name = "category")
    @Schema(description = "Category of the FAQ", example = "Order Management")
    private String category;

}
