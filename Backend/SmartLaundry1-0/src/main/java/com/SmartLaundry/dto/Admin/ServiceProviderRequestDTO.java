package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.SchedulePlan;
import com.SmartLaundry.model.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.SmartLaundry.dto.Admin.PriceDTO;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceProviderRequestDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNo;
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

    private ServiceProviderRequestDTO.AddressDTO addresses;
    private ServiceProviderRequestDTO.BankAccountDTO bankAccount;
    private List<PriceDTO> priceDTO;


    public AddressDTO getAddress() {
        return addresses;
    }

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

}
