package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.dto.Admin.AdminEditProfileRequestDTO;
import com.SmartLaundry.model.GENDER;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestProfileDTO {
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private LocalDate dateOfBirth;
    private String vehicleNumber;
    private byte[] aadharCardPhoto;
    private byte[] panCardPhoto;
    private byte[] drivingLicensePhoto;
    private String bankName;
    private String accountHolderName;
    private String bankAccountNumber;
    private String ifscCode;
    private byte[] profilePhoto;
    private GENDER gender;

    private RequestProfileDTO.AddressDTO addresses;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressDTO implements Serializable {
        private String name;
        private String areaName;
        private String pincode;
        private String cityName;
    }
}
