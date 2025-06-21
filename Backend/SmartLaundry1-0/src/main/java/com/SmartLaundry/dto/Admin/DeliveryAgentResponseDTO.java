package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.dto.AddressDTO;
import com.SmartLaundry.model.GENDER;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAgentResponseDTO {
    private String userId;
    private String deliveryAgentId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private LocalDateTime joinedAt;
    private LocalDate dateOfBirth;
    private String vehicleNumber;
    private String profilePhoto;
    private String aadharCardPhoto;
    private String panCardPhoto;
    private String drivingLicensePhoto;
    private String bankName;
    private String accountHolderName;
    private String bankAccountNumber;
    private String ifscCode;
    private GENDER gender;

    private DeliveryAgentResponseDTO.AddressDTO address;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressDTO {
        private String name;
        private String areaName;
        private String pincode;
        private String cityName;
    }
}
