package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_addresses")
@Schema(description = "Represents a user address with unique ID")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "add_id")
    @Schema(description = "Unique udentifier of the address.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long addressId;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Id of the user", example = "US00001", accessMode = Schema.AccessMode.READ_ONLY)
    private Users users;

    @NotBlank(message = "Name is required.")
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "The name of the address.", example = "B-201/KP Residency")
    private String name;

    @NotBlank(message = "Area name is required.")
    @Column(name = "area_name", nullable = false,  length = 100)
    @Schema(description = "The area name of the address.", example = "Bapunagar")
    private String areaName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    @Schema(description = "City id of the address", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private City city;

    @NotBlank(message = "Pincode is required.")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    @Column(name = "pincode", nullable = false, length = 6)
    @Schema(description = "The pincode of the address.", example = "380024")
    private String pincode;

    @Override
    public String toString() {
        return "UserAddress{" +
                "addressId=" + addressId +
                ", name='" + name + '\'' +
                ", areaName='" + areaName + '\'' +
                ", pincode='" + pincode + '\'' +
                // exclude 'user' field here
                '}';
    }


}
