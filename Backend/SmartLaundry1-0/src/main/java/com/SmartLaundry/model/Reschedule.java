package com.SmartLaundry.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.nio.CharBuffer;
import java.time.LocalDate;

@Entity
@Table(name = "RESCHEDULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reschedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reschedule_Id")
    private Long rescheduleId;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Slot", nullable = false)
    private String slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Order_Id", nullable = false)
    private Order order;


}