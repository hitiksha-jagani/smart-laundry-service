package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_status_history")
@Schema(description = "Represents an order status of the orders.")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier for the order status.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "Reference to the the order.")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Status of the order", example = "PLACED")
    private OrderStatus status;

    @Column(name = "changed_at", nullable = false)
    @Schema(description = "Date and time of changed status.", example = "2025-05-31 00:00:00")
    private LocalDateTime changedAt;
}
