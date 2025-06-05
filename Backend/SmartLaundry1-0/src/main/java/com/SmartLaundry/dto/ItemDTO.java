package com.SmartLaundry.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private String serviceName;
    private String subServiceName;
    private String itemName;
}
