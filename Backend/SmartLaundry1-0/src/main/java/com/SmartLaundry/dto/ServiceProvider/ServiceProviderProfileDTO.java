package com.SmartLaundry.dto.ServiceProvider;
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
    private Set<SchedulePlan> schedulePlans;
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
