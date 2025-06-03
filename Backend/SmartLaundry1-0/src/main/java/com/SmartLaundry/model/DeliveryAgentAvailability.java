package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "delivery_agent_availability")
@Schema(description = "Represent weekly availability of delivery agent.")
public class DeliveryAgentAvailability implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "availability_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the availability.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long availabilityId;

    @NotBlank(message = "Day of week is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "dayOfWeek", nullable = false)
    @Schema(description = "Day of the week of the availability.", example = "MONDAY")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Date of the day is required.")
    @Column(name = "date", nullable = false)
    @Schema(description = "Date of the selected day.", example = "2025-06-02")
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    @Schema(description = "Start time of the day.", example = "09:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Schema(description = "End time of the day.", example = "09:00:00")
    private LocalTime endTime;

    @Column(name = "holiday", nullable = false)
    @Schema(description = "Selected day is holiday or not.", example = "True")
    private boolean holiday = false;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_agent_id", nullable = false)
    @Schema(description = "Id of the delivery agent.", example = "DA0001", accessMode = Schema.AccessMode.READ_ONLY)
    private DeliveryAgent deliveryAgent;
}
