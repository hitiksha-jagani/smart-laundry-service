package com.SmartLaundry.dto;
import com.SmartLaundry.model.SchedulePlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProviderProfileDTO {
    private String businessName;
    private String businessLicenseNumber;
    private String gstNumber;
    private Boolean needOfDeliveryAgent;
    private byte[] photoImage;
    private byte[] aadharCardImage;
    private byte[] panCardImage;
    private byte[] businessUtilityBillImage;
    private Set<SchedulePlan> schedulePlans;
    private BankAccountDTO bankAccount;
    private List<ItemPriceDTO> items;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankAccountDTO {
        private String bankName;
        private String ifscCode;
        private String bankAccountNumber;
        private String accountHolderName;
        private AddressDTO address;
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


    public byte[] getPhotoImageBase64() {
        return photoImage;
    }

    public byte[] getAadharCardImageBase64() {
        return aadharCardImage;
    }

    public byte[] getPanCardImageBase64() {
        return panCardImage;
    }

    public byte[] getBusinessUtilityBillImageBase64() {
        return businessUtilityBillImage;
    }
}
