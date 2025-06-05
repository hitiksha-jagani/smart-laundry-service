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
    private String faqId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    @Schema(description = "Reference to support ticket ID", example = "TKT123")
    private Ticket ticket;

    @NotNull
    @Column(name = "visibility_status", nullable = false)
    @Schema(description = "Whether FAQ is visible to users", example = "true")
    private Boolean visibilityStatus;

}
