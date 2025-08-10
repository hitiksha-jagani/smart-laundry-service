package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "promotion_excluded_cloths")
@Schema(description = "Links promotions to excluded cloth items.")
public class PromotionExcludedCloth implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;

}
