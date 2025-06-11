package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ManageServiceListingRequestDTO;
import com.SmartLaundry.service.Admin.ServiceListingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

// @author Hitiksha Jagani
@RestController
@RequestMapping("")
public class ServiceListingController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ServiceListingService serviceListingService;

    // http://localhost:8080/service-summary
    // Return count of services
//    public ResponseEntity<>



    // http://localhost:8080/add-items
    // Render a form to submit items
    @PostMapping("/add-items")
    public ResponseEntity<String> addItems(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                             HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addItemDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/add-services
    // Render a form to submit services
    @PostMapping("/add-services")
    public ResponseEntity<String> addService(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                             HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addServiceDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/add-subservices
    // Render a form to submit sub-service
    @PostMapping("/add-subservices")
    public ResponseEntity<String> addSubService(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                                HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addSubServiceDetails(userId, manageServiceListingRequestDTO));
    }

}
