package com.SmartLaundry.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Parameter;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "delivery_agent")
@Schema(description = "Represents a delivery agent with unique ID.")
public class DeliveryAgent implements Serializable {

    @Id
    @GeneratedValue(generator = "delivery-agent-id-generator")
    @GenericGenerator(
            name = "delivery-agent-id-generator",
            strategy = "com.SmartLaundry.util.GenericPrefixIdGenerator",
            parameters = {
                    @Parameter(name = "prefix", value = "DA"),
                    @Parameter(name = "table_name", value = "delivery_agent"),
                    @Parameter(name = "column_name", value = "delivery_agent_id"),
                    @Parameter(name = "number_length", value = "4")
            }
    )
    @Column(name = "delivery_agent_id", nullable = false, unique = true, updatable = false)
    @Schema(description = "Unique identifier for the delivery agents.", example = "DA0001", accessMode = Schema.AccessMode.READ_ONLY)
    private String deliveryAgentId;

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    @Schema(description = "User if of the delivery agent.", example = "US00001", accessMode = Schema.AccessMode.READ_ONLY)
    private Users users;

    @Column(name = "current_latitude", nullable = true)
    private Double currentLatitude;

    @Column(name = "current_longitude", nullable = true)
    private Double currentLongitude;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of Birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Date of birth of the delivery agent. Must be in past.", example = "2025-05-27")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Vehicle number is required.")
    @Size(min = 8, max = 10, message = "Vehicle number must be between 8 and 10 characters.")
    @Column(name = "vehicle_number", unique = true, nullable = false)
    @Schema(description = "Vehicle number of the delivery agent.", example = "GJ011234")
    private String vehicleNumber;


    @NotBlank(message = "Bank name is required.")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    @Column(name = "bank_name", nullable = false)
    @Schema(description = "Bank name of the delivery agent bank.", example = "SBI")
    private String bankName;

    @NotBlank(message = "Account holder name is required.")
    @Column(name = "account_holder_name", nullable = false)
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Account holder name contains invalid characters.")
    @Schema(description = "Account Holder name of the delivery agent bank.", example = "John Deo")
    private String accountHolderName;

    @NotBlank(message = "Bank account number is required.")
    @Column(name = "bank_account_number", nullable = false, unique = true)
    @Schema(description = "Account number of the delivery agent bank.", example = "84659032091")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC Code is required.")
    @Column(name = "ifsc_code", nullable = false)
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
    @Schema(description = "IFSC Code of the delivery agent bank.", example = "HDFC0ABCD12")
    private String ifscCode;

    @NotNull(message = "Gender is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    @Schema(description = "Gender of the delivery agent.", example = "MALE")
    private GENDER gender;

    @NotNull(message = "aadhar card is required.")
    @Column(name = "aadhar_card_photo", nullable = false)
    @Schema(description = "aadhar card of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private String aadharCardPhoto;

    @Column(name = "pan_card_photo")
    @Schema(description = "Pan card of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private String panCardPhoto;

    @NotNull(message = "Driving License is required.")
    @Column(name = "driving_license_photo", nullable = false)
    @Schema(description = "Driving License of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private String drivingLicensePhoto;

    @NotNull(message = "Profile photo is required.")
    @Column(name = "profile_photo", nullable = false)
    @Schema(description = "Profile photo of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private String profilePhoto;

    @NotNull(message = "Status is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "Delivery agent is accepted or rejected.", example = "ACCEPTED")
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "deliveryAgent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JsonManagedReference
    private List<DeliveryAgentAvailability> deliveryAgentAvailabilities;


    public String getDeliveryAgentId() {
        return this.deliveryAgentId;
    }

}

