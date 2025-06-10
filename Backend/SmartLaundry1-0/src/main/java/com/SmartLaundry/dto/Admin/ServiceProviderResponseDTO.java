package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.model.SchedulePlan;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProviderResponseDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String businessName;
    private String businessLicenseNumber;
    private String gstNumber;
    private Boolean needOfDeliveryAgent;
    private String profilePhoto;
    private String aadharCardPhoto;
    private String panCardPhoto;
    private String businessUtilityBillPhoto;
    private Set<SchedulePlan> schedulePlans;

    private ServiceProviderResponseDTO.AddressDTO addresses;
    private ServiceProviderResponseDTO.BankAccountDTO bankAccount;
    private List<ServiceProviderResponseDTO.priceDTO> priceDTO;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressDTO {
        private String name;
        private String areaName;
        private String pincode;
        private String cityName;
        private Double latitude;
        private Double longitude;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankAccountDTO {
        private String bankName;
        private String ifscCode;
        private String bankAccountNumber;
        private String accountHolderName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class priceDTO {
        private String  itemId;
        private Long price;
        private String serviceProviderId;
    }
}
