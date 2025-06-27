package com.SmartLaundry.dto.ServiceProvider;

import lombok.AllArgsConstructor;
import lombok.Data;

// ServiceProviderDto.java
@Data
@AllArgsConstructor
public class ServiceProviderDto {
    private String serviceProviderId;
    private String email;
    private String phoneNumber;

    public ServiceProviderDto() {}



    // Getters and setters
}

