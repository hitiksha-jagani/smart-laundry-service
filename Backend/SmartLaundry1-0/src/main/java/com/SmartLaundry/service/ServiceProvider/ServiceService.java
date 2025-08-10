package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.model.Services;
import com.SmartLaundry.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }
}

