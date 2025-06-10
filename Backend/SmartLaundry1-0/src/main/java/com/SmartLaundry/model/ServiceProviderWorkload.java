package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "SERVICE_PROVIDER_WORKLOAD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProviderWorkload implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Workload_Id")
    private Long workloadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Order_Id", nullable = false)
    private Order order;

    @Column(name = "Action", nullable = false)
    private String action;

    @Column(name = "Active_Jobs", nullable = false)
    private Integer activeJobs;

    @Column(name = "Capacity", nullable = false)
    private Integer capacity;

    @Column(name = "Status", nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Provider_Id", nullable = false)
    private ServiceProvider serviceProvider;
}
