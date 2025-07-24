package com.SmartLaundry.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemDTO {
    private String itemName;
    private String serviceName;
    private String subServiceName;

    public void setService(String s) {
        this.serviceName = s;
    }

    public void setSubService(String s) {
        this.subServiceName = s;
    }
}

