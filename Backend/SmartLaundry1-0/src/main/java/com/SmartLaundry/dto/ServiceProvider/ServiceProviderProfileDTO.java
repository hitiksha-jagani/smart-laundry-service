package com.SmartLaundry.dto.ServiceProvider;
import com.SmartLaundry.model.SchedulePlan;
import lombok.*;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProviderProfileDTO {
    private String businessName;
    private String businessLicenseNumber;
    private String gstNumber;
    private Boolean needOfDeliveryAgent;
    private Set<SchedulePlan> schedulePlans;
    private List<ItemPriceDTO> items;

    private String photoImageBase64;
    private String AadharCardImageBase64;
    private String PanCardImageBase64;
    private String BusinessUtilityBillImageBase64;


    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class BankAccountDTO {
        private String bankName;
        private String ifscCode;
        private String bankAccountNumber;
        private String accountHolderName;
    }
    private BankAccountDTO bankAccount;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class ItemPriceDTO {
        private String serviceName;
        private String subServiceName;
        private String itemName;
        private Long price;
    }

}
