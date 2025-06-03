package com.SmartLaundry.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PREPAID_PLAN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PrepaidPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Prepaid_Plan_Id")
    private Long prepaidPlanId;

    @Column(name = "Balance", nullable = false)
    private Double balance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Payment_Id", nullable = false)
    private Payment payment;
}

