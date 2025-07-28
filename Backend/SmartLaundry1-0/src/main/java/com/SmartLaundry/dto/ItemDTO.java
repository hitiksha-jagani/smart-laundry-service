package com.SmartLaundry.dto;
import com.SmartLaundry.model.Items;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemDTO {
    private String itemId;
    private String itemName;
    private ServiceDTO service;
    private SubServiceDTO subService;

    // constructors, getters

    public ItemDTO(Items item) {
        this.itemId = item.getItemId();
        this.itemName = item.getItemName();
        this.service = new ServiceDTO(item.getService());
        this.subService = item.getSubService() != null ? new SubServiceDTO(item.getSubService()) : null;
    }

//    public void setService(String s) {
//        this.serviceName = s;
//    }
//
//    public void setSubService(String s) {
//        this.subServiceName = s;
//    }
}

