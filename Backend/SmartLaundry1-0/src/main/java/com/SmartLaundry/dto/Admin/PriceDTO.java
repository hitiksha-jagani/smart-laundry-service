package com.SmartLaundry.dto.Admin;

import com.SmartLaundry.dto.ItemDTO;
import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Services;
import com.SmartLaundry.model.SubService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceDTO {

//    private String id;

    @NotNull(message = "Price is required.")
    private Long price;

    private ItemDTO item;

   @JsonIgnore
    private ServiceProviderRequestDTO serviceProvider;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemDTO{
        private String itemId;
        private String itemName;
        private String serviceId;
        private String serviceName;
        private String subServiceId;
        private String subServiceName;
    }
}
