package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

//@author Hitiksha Jagani
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "delivery_agent_availability")
@Schema(description = "Represent weekly availability of delivery agent.")
public class DeliveryAgentAvailability implements Serializable {

    @Id
    @GeneratedValue(generator = "delivery-agent-availability-id-generator")
    @GenericGenerator(
            name = "delivery-agent-availability-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "prefix", value = "AV"),
                    @org.hibernate.annotations.Parameter(name = "table_name", value = "delivery_agent_availability"),
                    @org.hibernate.annotations.Parameter(name = "column_name", value = "availability_id"),
                    @org.hibernate.annotations.Parameter(name = "number_length", value = "4")
            }
    )
    @Column(name = "availability_id", updatable = false)
    @Schema(description = "Unique identifier for the availability.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private String availabilityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dayOfWeek", nullable = false)
    @Schema(description = "Day of the week of the availability.", example = "MONDAY")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Date of the day is required.")
    @Column(name = "date", nullable = false)
    @Schema(description = "Date of the selected day.", example = "2025-06-02")
    private LocalDate date;

    @Column(name = "start_time", nullable = true)
    @Schema(description = "Start time of the day.", example = "09:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = true)
    @Schema(description = "End time of the day.", example = "09:00:00")
    private LocalTime endTime;

    @Column(name = "holiday", nullable = true)
    @Schema(description = "Selected day is holiday or not.", example = "True")
    private boolean holiday = false;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_agent_id", nullable = false)
    @JsonBackReference
    private DeliveryAgent deliveryAgent;

}
