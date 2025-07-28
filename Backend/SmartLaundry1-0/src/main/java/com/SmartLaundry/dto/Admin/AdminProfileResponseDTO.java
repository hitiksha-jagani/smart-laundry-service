package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdminProfileResponseDTO implements Serializable {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    private AddressDTO addresses;

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
