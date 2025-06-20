package com.SmartLaundry.dto.ServiceProvider;
import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.model.SchedulePlan;
import com.SmartLaundry.model.UserAddress;
import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local;
import lombok.*;

import java.util.List;
import java.util.Set;


@Getter
@Setter
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
    private Set<SchedulePlan> schedulePlans;
    private String photoImage;
    private String AadharCardImage;
    private String PanCardImage;
    private String BusinessUtilityBillImage;
    private ServiceProviderProfileDTO.AddressDTO address;
    private ServiceProviderProfileDTO.BankAccountDTO bankAccount;
    private List<priceDTO> priceDTO;


    public String getProfilePhoto()
    {
        return photoImage;
    }
    public String getAadharCardPhoto()
    {
        return AadharCardImage;
    }
    public String getPanCardPhoto()
    {
        return PanCardImage;
    }
    public String getBusinessUtilityBillPhoto()
    {
        return BusinessUtilityBillImage;
    }

    public AddressDTO getAddress() {
        return address;
    }


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


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankAccountDTO {
        private String bankName;
        private String ifscCode;
        private String bankAccountNumber;
        private String accountHolderName;
        public BankAccountDTO(BankAccountDTO bankAccount) {
            this.bankName = bankAccount.getBankName();
            this.ifscCode = bankAccount.getIfscCode();
            this.bankAccountNumber = bankAccount.getBankAccountNumber();
            this.accountHolderName = bankAccount.getAccountHolderName();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class priceDTO {
        private String  itemId;
        private String itemName;
        private Long price;
        private String serviceProviderId;
    }

}
