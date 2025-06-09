package com.SmartLaundry.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//@author Hitiksha Jagani
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "delivery_agent_images")
@Schema(description = "Represents a delivery agent images with unique ID.")
public class DeliveryAgentImages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    private Long imageId;

    @NotNull(message = "aadhar card is required.")
    @Lob
    @Column(name = "aadhar_card_photo", nullable = false)
    @Schema(description = "aadhar card of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private byte[] aadharCardPhoto;

    @Lob
    @Column(name = "pan_card_photo")
    @Schema(description = "Pan card of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private byte[] panCardPhoto;

    @NotNull(message = "Driving License is required.")
    @Lob
    @Column(name = "driving_license_photo", nullable = false)
    @Schema(description = "Driving License of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private byte[] drivingLicensePhoto;

    @NotNull(message = "Profile photo is required.")
    @Lob
    @Column(name = "profile_photo", nullable = false)
    @Schema(description = "Profile photo of the delivery agent. Must be in png/jpeg format.", example = "photo.png")
    private byte[] profilePhoto;

    @JsonIgnore
    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_agent_id", nullable = false)
    @Schema(description = "Id of the delivery agent.", example = "DA0001", accessMode = Schema.AccessMode.READ_ONLY)
    private DeliveryAgent deliveryAgent;
}
