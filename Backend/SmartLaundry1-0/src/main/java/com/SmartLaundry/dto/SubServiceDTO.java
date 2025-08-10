package com.SmartLaundry.dto;

import com.SmartLaundry.model.SubService;

public class SubServiceDTO {
    private String subServiceId;
    private String subServiceName;

    public SubServiceDTO() {}

    public SubServiceDTO(SubService subService) {
        this.subServiceId = subService.getSubServiceId();
        this.subServiceName = subService.getSubServiceName();
    }

    // Getters and setters
    public String getSubServiceId() {
        return subServiceId;
    }

    public void setSubServiceId(String subServiceId) {
        this.subServiceId = subServiceId;
    }

    public String getSubServiceName() {
        return subServiceName;
    }

    public void setSubServiceName(String subServiceName) {
        this.subServiceName = subServiceName;
    }
}
