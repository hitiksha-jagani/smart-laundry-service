package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.CustomerResponseDTO;
import com.SmartLaundry.dto.Admin.DeliveryAgentResponseDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderResponseDTO;
import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.AdminUserService;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/users/customer/graphs
    // Return graph based overview of customers.
    @GetMapping("/customers/graphs")
    public ResponseEntity<?> getGraphBasedOverviewForCustomers(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(adminUserService.getGraphsForUsers(UserRole.CUSTOMER));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/customers/table
    // Return table based data of customers based on searches or filter.
    @GetMapping("/customers/table")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "id") String sortBy
    ) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        List<CustomerResponseDTO> customers = adminUserService.getFilteredCustomers(keyword, startDate, endDate, sortBy);
        return ResponseEntity.ok(customers);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/customers/table/block/{userId}
    // Block unblocked user.
    @PutMapping("/customers/table/block/{userId}")
    public ResponseEntity<String> blockCustomer(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.toggleUserBlockStatus(userId, true);
        return ResponseEntity.ok("Customer blocked successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/customers/table/unblock/{userId}
    // Unblock blocked user.
    @PutMapping("/customers/table/unblock/{userId}")
    public ResponseEntity<String> unblockCustomer(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.toggleUserBlockStatus(userId, false);
        return ResponseEntity.ok("Customer unblocked successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/customer/table/delete/{userId}
    // Delete user.
    @DeleteMapping("/customer/table/delete/{userId}")
    public ResponseEntity<String> deleteCustomer(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.deleteCustomer(userId);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/service-providers/graphs
    // Return graph based overview of service providers.
    @GetMapping("/service-providers/graphs")
    public ResponseEntity<?> getGraphBasedOverviewForServiceProvider(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(adminUserService.getGraphsForUsers(UserRole.SERVICE_PROVIDER));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/service-providers/table
    // Return table based data of service providers based on searches or filter.
    @GetMapping("/service-providers/table")
    public ResponseEntity<List<ServiceProviderResponseDTO>> getAllServiceProviders(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "id") String sortBy
    ) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        List<ServiceProviderResponseDTO> serviceProviders = adminUserService.getFilteredServiceProviders(keyword, startDate, endDate, sortBy);
        return ResponseEntity.ok(serviceProviders);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/service-providers/table/block/{userId}
    // Block unblocked user.
    @PutMapping("/service-providers/table/block/{userId}")
    public ResponseEntity<String> blockServiceProvider(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.toggleUserBlockStatus(userId, true);
        return ResponseEntity.ok("Service provider blocked successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/service-providers/table/unblock/{userId}
    // Unblock blocked user.
    @PutMapping("/service-providers/table/unblock/{userId}")
    public ResponseEntity<String> unblockServiceProvider(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.toggleUserBlockStatus(userId, false);
        return ResponseEntity.ok("Service provider unblocked successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/service-providers/table/delete/{providerId}
    // Delete user.
    @DeleteMapping("/service-providers/table/delete/{providerId}")
    public ResponseEntity<String> deleteServiceProvider(HttpServletRequest request, @PathVariable String providerId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.deleteServiceProvider(providerId);
        return ResponseEntity.ok("Service provider deleted successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/delivery-agents/graphs
    // Return graph based overview of delivery agents.
    @GetMapping("/delivery-agents/graphs")
    public ResponseEntity<?> getGraphBasedOverviewForDeliveryAgent(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(adminUserService.getGraphsForUsers(UserRole.DELIVERY_AGENT));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/delivery-agents/table
    // Return table based data of delivery agents based on searches or filter.
    @GetMapping("/delivery-agents/table")
    public ResponseEntity<List<DeliveryAgentResponseDTO>> getAllDeliveryAgents(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "id") String sortBy
    ) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        List<DeliveryAgentResponseDTO> serviceProviders = adminUserService.getFilteredDeliveryAgents(keyword, startDate, endDate, sortBy);
        return ResponseEntity.ok(serviceProviders);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/delivery-agents/table/block/{userId}
    // Block unblocked user.
    @PutMapping("/delivery-agents/table/block/{userId}")
    public ResponseEntity<String> blockDeliveryAgent(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.toggleUserBlockStatus(userId, true);
        return ResponseEntity.ok("Delivery agent blocked successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/delivery-agents/table/unblock/{userId}
    // Unblock blocked user.
    @PutMapping("/delivery-agents/table/unblock/{userId}")
    public ResponseEntity<String> unblockDeliveryAgent(HttpServletRequest request, @PathVariable String userId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.toggleUserBlockStatus(userId, false);
        return ResponseEntity.ok("Delivery agent unblocked successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/users/delivery-agents/table/delete/{agentId}
    // Delete user.
    @DeleteMapping("/delivery-agents/table/delete/{agentId}")
    public ResponseEntity<String> deleteDeliveryAgent(HttpServletRequest request, @PathVariable String agentId) throws AccessDeniedException {
        String adminId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(adminId);
        roleCheckingService.isAdmin(user);
        adminUserService.deleteDeliveryAgent(agentId);
        return ResponseEntity.ok("Delivery agent deleted successfully");
    }

}
