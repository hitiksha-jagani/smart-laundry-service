package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ManageServiceListingRequestDTO;
import com.SmartLaundry.dto.Admin.ServiceSummaryDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.ServiceListingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(HttpServletRequest request) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        List<ServiceSummaryDTO> summary = serviceListingService.getServiceSummary();
        return ResponseEntity.ok(summary);
    }

    // http://localhost:8080/service/add-items
    // Render a form to submit items
    @PostMapping("/add-items")
    public ResponseEntity<String> addItems(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                             HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addItemDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/service/add-services
    // Render a form to submit services
    @PostMapping("/add-services")
    public ResponseEntity<String> addService(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                             HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addServiceDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/service/get-services
    @GetMapping("/get-services")
    public ResponseEntity<List<String>> getService(HttpServletRequest request)  {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.getServices(userId));
    }

    // http://localhost:8080/service/add-subservices
    // Render a form to submit sub-service
    @PostMapping("/add-subservices")
    public ResponseEntity<String> addSubService(@Valid @RequestBody ManageServiceListingRequestDTO manageServiceListingRequestDTO,
                                                HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceListingService.addSubServiceDetails(userId, manageServiceListingRequestDTO));
    }

    // http://localhost:8080/service/get-subservices/{serviceName}
    @GetMapping("/get-subservices/{serviceName}")
    public ResponseEntity<List<String>> getSubService(HttpServletRequest request, @PathVariable String serviceName)  {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        List<String> subServices = serviceListingService.getSubServiceNamesByServiceName(serviceName);
        return ResponseEntity.ok(subServices);
    }
}
