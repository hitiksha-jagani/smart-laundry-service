package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "services", uniqueConstraints = {
    @UniqueConstraint(columnNames = "service_name", name = "uk_service_name")
})
@Schema(description = "Represents a service with a unique ID and name.")
public class Services implements Serializable {

    @Id
    @GeneratedValue(generator = "service-id-generator")
    @GenericGenerator(
            name = "service-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "SV"),
                    @Parameter(name = "table_name", value = "services"),
                    @Parameter(name = "column_name", value = "service_id"),
                    @Parameter(name = "number_length", value = "3")
            }
    )
    @Column(name = "service_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the service.", example = "SV001", accessMode = Schema.AccessMode.READ_ONLY)
    private String serviceId;

    @NotBlank(message = "Service name is required.")
    @Size(min = 3, max = 100, message = "Service name must be between 3 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z+\\s\\-_()]+$", message = "Service name contains invalid characters.")
    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    @Schema(description = "The name of the service. Must be unique.", example = "Dry Cleaning")
    private String serviceName;

    @JsonBackReference
    @OneToMany(mappedBy = "services", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubService> subServices = new ArrayList<>();

    @ManyToMany(mappedBy = "services")
    private Set<ServiceProvider> serviceProviders = new HashSet<>();
}
