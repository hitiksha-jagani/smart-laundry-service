package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.model.SchedulePlan;
import com.SmartLaundry.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/schedule-plans")
public class SchedulePlanController {

    private final ServiceProviderRepository serviceProviderRepository;

    public SchedulePlanController(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<Set<SchedulePlan>> getSchedulePlansForProvider(@PathVariable String providerId) {
        return serviceProviderRepository.findByServiceProviderId(providerId)
                .map(provider -> ResponseEntity.ok(provider.getSchedulePlans()))
                .orElse(ResponseEntity.notFound().build());
    }

}
