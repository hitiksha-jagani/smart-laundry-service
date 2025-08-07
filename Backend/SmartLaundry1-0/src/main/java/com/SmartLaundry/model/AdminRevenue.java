package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_revenue")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminRevenue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "admin_revenue_id", nullable = false)
    private Long adminRevenueId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    @Schema(description = "Payment reference ID", example = "PAY12345")
    private Payment payment;

    @Column(name = "profit_from_delivery_agent")
    private Double profitFromDeliveryAgent;

    @Column(name = "profit_from_service_provider")
    private Double profitFromServiceProvider;

    @Column(name = "total_revenue")
    private Double totalRevenue;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
