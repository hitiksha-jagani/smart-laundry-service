package com.SmartLaundry.controller.ServiceProvider;

// ServiceProviderController.java
import com.SmartLaundry.dto.ServiceProvider.ServiceProviderDto;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.ServiceProvider.ProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/provider")
public class ProviderController {

    private final ProviderService serviceProviderService;

    public ProviderController(ProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping(value = "/{providerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProvider(@PathVariable String providerId) {
        try {
            Optional<ServiceProvider> optional = serviceProviderService.getById(providerId);
            if (optional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider not found");

            ServiceProvider provider = optional.get();
            Users user = provider.getUser(); // <- retrieve user

            ServiceProviderDto dto = new ServiceProviderDto(
                    provider.getServiceProviderId(),
                    user.getEmail(),            // <-- use user.getEmail()
                    user.getPhoneNo()           // <-- use user.getPhoneNo()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}

