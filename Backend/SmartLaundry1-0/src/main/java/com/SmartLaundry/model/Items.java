package com.SmartLaundry.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
@Schema(description = "Represents a items.")
public class Items implements Serializable {

    @Id
    @GeneratedValue(generator = "item-id-generator")
    @GenericGenerator(
            name = "item-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "IT"),
                    @Parameter(name = "table_name", value = "items"),
                    @Parameter(name = "column_name", value = "item_id"),
                    @Parameter(name = "number_length", value = "3")
            }
    )
    @Column(name = "item_id", updatable = false, nullable = false)
    @Schema(description = "Unique identifier for the items.", example = "IT001", accessMode = Schema.AccessMode.READ_ONLY)
    private String itemId;

    @NotBlank(message = "Item name is required.")
    @Size(min = 3, max = 100, message = "Item name must be between 3 and 100 characters.")
    @Pattern(regexp = "^[A-Za-z-()\\\\s]+$", message = "Item name contains invalid characters.")
    @Column(name = "item_name", nullable = false, unique = false, length = 100)
    @Schema(description = "The name of the item.", example = "T-shirt")
    private String itemName;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id", nullable = false)
    private Services service;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sub_service_id", nullable = false)
    private SubService subService;

}