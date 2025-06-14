package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.List;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bill")
@Schema(description = "Represents the bill for an order.")
public class Bill implements Serializable {

    @Id
    @GeneratedValue(generator = "invoice-number-generator")
    @GenericGenerator(
            name = "invoice-number-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "IN"),
                    @Parameter(name = "table_name", value = "bill"),
                    @Parameter(name = "column_name", value = "invoice_number"),
                    @Parameter(name = "number_length", value = "4")
            }
    )
    @Column(name = "invoice_number", nullable = false, updatable = false)
    @Schema(description = "Unique invoice number.", example = "INV0001")
    private String invoiceNumber;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "The order associated with this bill.")
    private Order order;

    @JsonBackReference
    @OneToMany(mappedBy = "bill")
    private List<BookingItem> bookingItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "Status of the bill.", example = "PAID")
    private BillStatus status = BillStatus.PENDING;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "items_total_price", nullable = false)
    @Schema(description = "Total price of items before delivery and taxes.", example = "300.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double itemsTotalPrice;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "delivery_charge", nullable = false)
    @Schema(description = "Delivery charge applied to the order.", example = "30.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double deliveryCharge;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "gst_amount", nullable = false)
    @Schema(description = "GST amount applied to the order.", example = "18.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double gstAmount;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "discount_amount", nullable = false)
    @Schema(description = "Discount amount subtracted from total.", example = "10.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double discountAmount;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "final_price", nullable = false)
    @Schema(description = "Final payable price after all calculations.", example = "338.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double finalPrice;

    @OneToOne(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Payment associated with this bill.")
    private Payments payment;


}

