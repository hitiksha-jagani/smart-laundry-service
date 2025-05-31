package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "booking_items")
@Schema(description = "Represents an item booked under a specific order.")
public class BookingItem implements Serializable {

    @Id
    @GeneratedValue(generator = "booking-item-id-generator")
    @GenericGenerator(
            name = "booking-item-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "BI"),
                    @Parameter(name = "table_name", value = "booking_items"),
                    @Parameter(name = "column_name", value = "booking_item_id"),
                    @Parameter(name = "number_length", value = "5")
            }
    )

    @Column(name = "booking_item_id", nullable = false, updatable = false)
    @Schema(description = "Unique ID for the booking item.", example = "BI00001")
    private String bookingItemId;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "The associated order.")
    private Order order;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @Schema(description = "The item being booked.")
    private Items item;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Column(name = "quantity", nullable = false)
    @Schema(description = "Number of units booked.", example = "3")
    private Integer quantity;

    @NotNull(message = "Final price is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Final price cannot be negative.")
    @Column(name = "final_price", nullable = false)
    @Schema(description = "Final price for the item(s).", example = "150.00", accessMode = Schema.AccessMode.READ_ONLY)
    private Double finalPrice;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_number", nullable = false)
    @Schema(description = "The booking item associated with this bill.")
    private Bill bill;

}
