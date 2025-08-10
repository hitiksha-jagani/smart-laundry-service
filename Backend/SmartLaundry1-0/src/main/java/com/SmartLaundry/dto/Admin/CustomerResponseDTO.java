package com.SmartLaundry.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponseDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private LocalDateTime joinAt;

    private CustomerResponseDTO.AddressDTO addresses;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AddressDTO{
        private String name;
        private String areaName;
        private String pincode;
        private String cityName;
    }

}

