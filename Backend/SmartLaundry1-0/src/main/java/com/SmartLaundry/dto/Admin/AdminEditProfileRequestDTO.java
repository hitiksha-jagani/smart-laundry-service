package com.SmartLaundry.dto.Admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminEditProfileRequestDTO {
    private String firstName;
    private String lastName;
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
