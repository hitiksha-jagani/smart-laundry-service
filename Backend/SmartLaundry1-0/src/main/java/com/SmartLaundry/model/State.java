package com.SmartLaundry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "states", uniqueConstraints = {
        @UniqueConstraint(columnNames = "state_name", name = "uk_state_name")
})
@Schema(description = "Represents a state with unique ID and name.")
public class State implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "state_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the states.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long stateId;

    @NotBlank(message = "State name is required.")
    @Size(max = 100, message = "State name must be less than 100 characters.")
    @Pattern(regexp = "^[A-Za-z\\\\s]+$", message = "State name contains invalid characters.")
    @Column(name = "state_name", unique = true, nullable = false, length = 100)
    @Schema(description = "The name of the state. Must be unique.", example = "Gujarat")
    private String stateName;

}

