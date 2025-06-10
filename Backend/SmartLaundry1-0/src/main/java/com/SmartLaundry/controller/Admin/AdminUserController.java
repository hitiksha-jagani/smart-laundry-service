package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.CustomerResponseDTO;
import com.SmartLaundry.dto.Admin.DeliveryAgentResponseDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderResponseDTO;
import com.SmartLaundry.service.Admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // @author Hitiksha Jagani
    // http://localhost:8080/customer/graphs
    // Return graph based overview of customers.

    // @author Hitiksha Jagani
    // http://localhost:8080/customers/table
    // Return table based data of customers based on searches or filter.
    @GetMapping("/customers/table")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<CustomerResponseDTO> customers = adminUserService.getFilteredCustomers(keyword, startDate, endDate, sortBy);
        return ResponseEntity.ok(customers);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/service-providers/graphs
    // Return graph based overview of service providers.

    // @author Hitiksha Jagani
    // http://localhost:8080/service-providers/table
    // Return table based data of service providers based on searches or filter.
    @GetMapping("/service-providers/table")
    public ResponseEntity<List<ServiceProviderResponseDTO>> getAllServiceProviders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ServiceProviderResponseDTO> serviceProviders = adminUserService.getFilteredServiceProviders(keyword, startDate, endDate, sortBy);
        return ResponseEntity.ok(serviceProviders);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/delivery-agents/graphs
    // Return graph based overview of delivery agents.

    // @author Hitiksha Jagani
    // http://localhost:8080/delivery-agents/table
    // Return table based data of delivery agents based on searches or filter.
    @GetMapping("/delivery-agents/table")
    public ResponseEntity<List<DeliveryAgentResponseDTO>> getAllDeliveryAgents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<DeliveryAgentResponseDTO> serviceProviders = adminUserService.getFilteredDeliveryAgents(keyword, startDate, endDate, sortBy);
        return ResponseEntity.ok(serviceProviders);
    }

}
