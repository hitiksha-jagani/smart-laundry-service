package com.SmartLaundry.dto.DeliveryAgent;

import com.SmartLaundry.model.GENDER;
import com.SmartLaundry.model.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAgentProfileDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
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
    private Double currentLatitude;
    private Double currentLongitude;

    private AddressDTO address;

    @Data
    @Builder
    @AllArgsConstructor
    public static class AddressDTO {
        private String name;
        private String areaName;
        private String pincode;
        private String cityName;
        private Double latitude;
        private Double longitude;

//        public AddressDTO(String name, String areaName, String pincode, String cityName, Double latitude, Double longitude) {
//            this.name = name;
//            this.areaName = areaName;
//            this.pincode = pincode;
//            this.cityName = cityName;
//            this.latitude = latitude;
//            this.longitude = longitude;
//        }

        public AddressDTO() {
        }

        public AddressDTO(UserAddress userAddress) {
            this.name = userAddress.getName();
            this.areaName = userAddress.getAreaName();
            this.pincode = userAddress.getPincode();
            this.cityName = userAddress.getCity().getCityName();
            this.latitude = userAddress.getLatitude();
            this.longitude = userAddress.getLongitude();
        }
    }
}
