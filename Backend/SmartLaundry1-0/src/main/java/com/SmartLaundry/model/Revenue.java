package com.SmartLaundry.model;

import jakarta.persistence.*;

@Entity
@Table(name = "revenue")
public class Revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Long revenueId;

    @Column(name = "service_provider")
    private Double serviceProvider;

    @Column(name = "delivery_agent")
    private Double deliveryAgent;
}
