package com.SmartLaundry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "revenue_breakdown")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueBreakDown {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Long revenueId;

    @Column(name = "service_provider")
    private Double serviceProvider;

    @Column(name = "delivery_agent")
    private Double deliveryAgent;

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
