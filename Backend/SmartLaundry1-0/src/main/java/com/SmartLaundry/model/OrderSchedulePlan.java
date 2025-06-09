package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_schedule_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "order")
@Schema(description = "Represents the schedule plan associated with a customer's order.")
public class OrderSchedulePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderPlan_Id")
    private Long OrderPlanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchedulePlan schedulePlan = SchedulePlan.NONE;

    @Column(nullable = false)
    private boolean payEachDelivery;

    @Column(nullable = false)
    private boolean payLastDelivery;

//    @OneToOne(optional = false)
//    @JoinColumn(name = "order_id", unique = true, nullable = false)
//    @JsonIgnore
//    private Order order;
    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    @JsonBackReference
    private Order order;

}
