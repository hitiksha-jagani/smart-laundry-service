package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ManageServiceListingRequestDTO;
import com.SmartLaundry.service.Admin.ServiceListingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

// @author Hitiksha Jagani
@RestController
@RequestMapping("/service")
public class ServiceListingController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ServiceListingService serviceListingService;

    // http://localhost:8080/service/summary
    // Return count of services
    @GetMapping("/service/summary")
    public ResponseEntity<?> getSummary(HttpServletRequest request) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok("Hello");
    }

    // http://localhost:8080/service/add-items
    // Render a form to submit items
    @PostMapping("/service/add-items")
    public ResponseEntity<String> addItems(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                             HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addItemDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/service/add-services
    // Render a form to submit services
    @PostMapping("/service/add-services")
    public ResponseEntity<String> addService(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                             HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addServiceDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/service/add-subservices
    // Render a form to submit sub-service
    @PostMapping("/service/add-subservices")
    public ResponseEntity<String> addSubService(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                                HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addSubServiceDetails(userId, manageServiceListingRequestDTO));
    }

}
