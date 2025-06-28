package com.SmartLaundry.controller;

import com.SmartLaundry.model.Services;
import com.SmartLaundry.service.ServiceProvider.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService; // This should be your service layer

    @GetMapping
    public List<Services> getAllServices() {
        return serviceService.getAllServices(); // Implement this method in your service layer
    }
}


