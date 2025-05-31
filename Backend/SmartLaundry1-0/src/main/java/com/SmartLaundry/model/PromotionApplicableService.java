package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "promotion_applicable_services")
@Schema(description = "Links promotions to applicable services.")
public class PromotionApplicableService implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique promotion applicable service ID", example = "1")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    @Schema(description = "Promotion applicable services for promotion")
    private Promotion promotion;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @Schema(description = "Services for which promotion is applicable.")
    private Services service;

}
