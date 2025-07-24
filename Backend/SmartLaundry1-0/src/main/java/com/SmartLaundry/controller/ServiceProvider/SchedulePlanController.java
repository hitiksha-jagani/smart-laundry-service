package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.model.SchedulePlan;
import com.SmartLaundry.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/schedule-plans")
public class SchedulePlanController {

    private final ServiceProviderRepository serviceProviderRepository;

    public SchedulePlanController(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllSchedulePlans() {
        List<String> plans = Arrays.stream(SchedulePlan.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<Set<SchedulePlan>> getSchedulePlansForProvider(@PathVariable String providerId) {
        return serviceProviderRepository.findByServiceProviderId(providerId)
                .map(provider -> ResponseEntity.ok(provider.getSchedulePlans()))
                .orElse(ResponseEntity.notFound().build());
    }

}
