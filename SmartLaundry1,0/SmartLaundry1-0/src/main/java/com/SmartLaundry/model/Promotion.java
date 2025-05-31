package com.SmartLaundry.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "promotion")
@Schema(description = "Represents a promotional discount campaign.")
public class Promotion implements Serializable {

    @Id
    @GeneratedValue(generator = "promotion-id-generator")
    @GenericGenerator(
            name = "promotion-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "PR"),
                    @Parameter(name = "table_name", value = "promotion"),
                    @Parameter(name = "column_name", value = "promotion_id"),
                    @Parameter(name = "number_length", value = "3")
            }    )
    @Column(name = "promotion_id", nullable = false, updatable = false)
    @Schema(description = "Unique promotion ID", example = "PR001")
    private String promotionId;

    @NotBlank(message = "Title is required.")
    @Column(name = "title", nullable = false, length = 100)
    @Schema(description = "Promotion title", example = "Diwali Offer")
    private String title;

    @Column(name = "description", length = 255)
    @Schema(description = "Promotion description", example = "20% off on all laundry services")
    private String description;

    @NotBlank(message = "Promo code is required.")
    @Column(name = "promo_code", unique = true, nullable = false, length = 50)
    @Schema(description = "Promotional code", example = "DIWALI20")
    private String promoCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    @Schema(description = "Discount type", example = "PERCENTAGE")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required.")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "discount_value", nullable = false)
    @Schema(description = "Discount value", example = "20.0")
    private Double discountValue;

    @DecimalMin(value = "0.0")
    @Column(name = "max_discount")
    @Schema(description = "Maximum discount allowed", example = "100.0")
    private Double maxDiscount;

    @DecimalMin(value = "0.0")
    @Column(name = "min_order_amount")
    @Schema(description = "Minimum order amount required", example = "300.0")
    private Double minOrderAmount;

    @NotNull(message = "Start date is required.")
    @Column(name = "start_date", nullable = false)
    @Schema(description = "Promotion start date", example = "2025-11-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    @Column(name = "end_date", nullable = false)
    @Schema(description = "Promotion end date", example = "2025-11-30")
    private LocalDate endDate;

}
