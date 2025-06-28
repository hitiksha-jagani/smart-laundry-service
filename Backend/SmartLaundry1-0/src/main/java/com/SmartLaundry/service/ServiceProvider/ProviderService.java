package com.SmartLaundry.service.ServiceProvider;
// ServiceProviderService.java
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public Optional<ServiceProvider> getById(String providerId) {
        return serviceProviderRepository.findById(providerId);
    }
}

