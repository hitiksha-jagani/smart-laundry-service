package com.SmartLaundry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_agent_earnings")
public class DeliveryAgentEarnings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "earning_id", nullable = false, updatable = false)
    private Long earningId;

    // Delivery agent get fixed amount for order total km <= baseKm
    @Column(name = "base_km")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base km cannot be negative or zero")
    private Double baseKm;

    // Delivery agent must get this amount for order total km <= baseKm
    @Column(name = "fixed_amount", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Fixed amount cannot be negative or zero")
    private Double fixedAmount;

    // Delivery agent get extra amount for each km > baseKm for order total km
    @Column(name = "extra_per_km_amount")
    @DecimalMin(value = "0.0", inclusive = false, message = "Extra per km amount cannot be negative or zero")
    private Double extraPerKmAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CurrentStatus currentStatus;

    @CreationTimestamp
    @Column(name = "crated_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "active_at")
    private LocalDateTime activeAt;

    @Column(name = "deactivate_at")
    private LocalDateTime deactivateAt;
}
