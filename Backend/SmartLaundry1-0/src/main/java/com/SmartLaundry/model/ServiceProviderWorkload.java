package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Provider_Id", nullable = false)
    private ServiceProvider serviceProvider;
}
