package com.SmartLaundry.dto;

import com.SmartLaundry.model.Services;

public class ServiceDTO {
    private String serviceId;
    private String serviceName;

    public ServiceDTO() {}

    public ServiceDTO(Services service) {
        this.serviceId = service.getServiceId();
        this.serviceName = service.getServiceName();
    }

    // Getters and setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
