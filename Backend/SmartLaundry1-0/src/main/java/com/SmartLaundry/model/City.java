package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "city", uniqueConstraints = {
    @UniqueConstraint(columnNames = "city_name", name = "uk_city_name")
})
@Schema(description = "Represents a city with unique ID, name.")
public class City implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "city_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the cities.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long cityId;

    @NotBlank(message = "City name is required.")
    @Size(max = 100, message = "City name must be less than 100 characters.")
    @Pattern(regexp = "^[A-Za-z\\\\s]+$", message = "City name contains invalid characters.")
    @Column(name = "city_name", unique = true, nullable = false, length = 100)
    @Schema(description = "The name of the city. Must be unique.", example = "Ahmedabad")
    private String cityName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;


}
