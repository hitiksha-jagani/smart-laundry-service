package com.SmartLaundry.dto;
import com.SmartLaundry.dto.DeliveryAgent.RequestProfileDTO;
import com.SmartLaundry.model.SchedulePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProviderProfileDTO {
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private String businessName;
    private String businessLicenseNumber;
    private String gstNumber;
    private Boolean needOfDeliveryAgent;
    private byte[] profilePhoto;
    private byte[] aadharCardPhoto;
    private byte[] panCardPhoto;
    private byte[] businessUtilityBillPhoto;
    private Set<SchedulePlan> schedulePlans;

    private ServiceProviderProfileDTO.AddressDTO addresses;
    private ServiceProviderProfileDTO.BankAccountDTO bankAccount;
    private List<ItemPriceDTO> items;

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
    public static class ItemPriceDTO {
        private String serviceName;
        private String subServiceName;
        private String itemName;
        private Long price;
    }

}
