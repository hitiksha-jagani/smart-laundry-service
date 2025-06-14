package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payout")
@Schema(description = "Payment settlements for service providers or delivery agents.")
public class Payout implements Serializable {

    @Id
    @GeneratedValue(generator = "payout-id-generator")
    @GenericGenerator(
            name = "payout-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "PYT"),
                    @Parameter(name = "table_name", value = "payouts"),
                    @Parameter(name = "column_name", value = "payout_id"),
                    @Parameter(name = "number_length", value = "5")
            }
    )
    @Column(name = "payout_id", nullable = false, updatable = false)
    @Schema(example = "PYT00001")
    private String payoutId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    @Schema(description = "Payment reference ID", example = "PAY12345")
    private Payment payment;

    @Column(name = "delivery_earning")
    private Double deliveryEarning;

    @Column(name = "charge")
    private Double charge;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "final_amount", nullable = false)
    @Schema(description = "Amount paid", example = "500.00")
    private Double finalAmount;

    @Column(name = "transaction_id")
    @Schema(description = "Bank transaction ID", example = "TXN98765")
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "Status of the payout.", example = "PENDING")
    private PayoutStatus status = PayoutStatus.PENDING;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User ID receiving the payout", example = "US00001")
    private Users users;

    @Column(name = "date_time")
    @Schema(description = "Date and time of payout", example = "2025-06-01T10:00:00")
    private LocalDateTime dateTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Timestamp of the payout creation.", example = "2025-05-21 00:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

}
