package com.SmartLaundry.controller;
import com.SmartLaundry.model.SubService;
import com.SmartLaundry.service.ServiceProvider.SubServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subservices")
@RequiredArgsConstructor
public class SubServiceController {
    private final SubServiceService subServiceService;

    @GetMapping
    public List<SubService> getByServiceId(@RequestParam String serviceId) {
        return subServiceService.getByServiceId(serviceId);
    }
}

