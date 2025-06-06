package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sub_service", uniqueConstraints = {
        @UniqueConstraint(columnNames = "sub_service_name", name = "uk_sub_service_name")
})
@Schema(description = "Represents a sub service with a unique ID and name.")
public class SubService implements Serializable{

    @Id
    @GeneratedValue(generator = "sub-service-id-generator")
    @GenericGenerator(
            name = "sub-service-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "SUBSV"),
                    @Parameter(name = "table_name", value = "sub_service"),
                    @Parameter(name = "column_name", value = "sub_service_id"),
                    @Parameter(name = "number_length", value = "3")
            }    )
    @Column(name = "sub_service_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the sub service.", example = "SUBSV001", accessMode = Schema.AccessMode.READ_ONLY)
    private String subServiceId;

    @NotBlank(message = "Sub service name is required.")
    @Size(min = 3, max = 100, message = "Sub service name must be between 3 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z+()\\s]+$", message = "Service name contains invalid characters.")
    @Column(name = "sub_service_name", nullable = false, unique = true, length = 100)
    @Schema(description = "The name of the sub service. Must be unique.", example = "Winter Cloths")
    private String subServiceName;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id", nullable = false)
    private Services services;

}
