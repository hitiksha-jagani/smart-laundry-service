package com.SmartLaundry.dto.ServiceProvider;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveOrderDto {
    private String itemName;
    private String service;
    private String subService;
    private int quantity;
}
