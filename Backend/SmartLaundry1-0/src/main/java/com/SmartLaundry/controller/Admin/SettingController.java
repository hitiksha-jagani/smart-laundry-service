package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.*;
import com.SmartLaundry.model.APIProviders;
import com.SmartLaundry.model.DeliveryAgentEarnings;
import com.SmartLaundry.model.GeocodingConfig;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.Admin.SettingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.*;

@RestController
@RequestMapping("/configurations")
public class SettingController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/providers
    // Get all api providers
    @GetMapping("/providers")
    public ResponseEntity<List<Map<String, String>>> getSupportedProviders() {
        List<Map<String, String>> providers = Arrays.stream(APIProviders.values())
                .map(p -> Map.of(
                        "name", p.name(),
                        "label", p.getLabel()
                ))
                .toList();

        return ResponseEntity.ok(providers);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/save
    // Save api key
    @PostMapping("/save")
    public ResponseEntity<String> saveSetting(@RequestBody Map<String, String> payload,
                                              HttpServletRequest request) throws AccessDeniedException {

        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);

        String provider = payload.get("provider");
        String apiKey = payload.get("apiKey");

        settingService.saveConfig(provider, apiKey, userId);
        return ResponseEntity.ok("Configuration saved successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/history
    // Fatch past api key
    @GetMapping("/history")
    public ResponseEntity<List<GeocodingConfig>> getAllConfigs(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);

        List<GeocodingConfig> configs = settingService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }


    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/complaint-category
    // Set complaint category
    @PostMapping("/complaint-category")
    public ResponseEntity<ComplaintCategoryResponseDTO> setComplaintCategory(
            HttpServletRequest request,
            @Valid @RequestBody ComplaintCategorySetDTO complaintCategorySetDTO) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.setComplaintCategory(complaintCategorySetDTO));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/revenue-breakdown
    // Set revenue breakdown
    @PutMapping("/revenue-breakdown")
    public ResponseEntity<RevenueSettingResponseDTO> setRevenue(HttpServletRequest request, @RequestBody @Valid RevenueSettingRequestDTO revenueSettingRequestDTO) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.setRevenue(revenueSettingRequestDTO));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/agent-earnings
    // Set delivery agent earnings
    @PostMapping("/agent-earnings")
    public ResponseEntity<String> setDeliveryAgentEarnings(HttpServletRequest request, @RequestBody @Valid DeliveryAgentEarningSettingRequestDTO deliveryAgentEarningSettingRequestDTO) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.setAgentEarnings(deliveryAgentEarningSettingRequestDTO));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/agent-earnings/history
    // Get list of delivery agent earnings entries
    @GetMapping("/agent-earnings/history")
    public ResponseEntity<List<DeliveryAgentEarnings>> getDeliveryAgentEarnings(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.getAgentEarnings());
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/agent-earnings/history/{id}
    // Change status of delivery agent earnings
    @PutMapping("/agent-earnings/history/{id}")
    public ResponseEntity<String> changeDeliveryAgentEarningStatus(HttpServletRequest request, @PathVariable Long id) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.changeAgentEarningStatus(id));
    }

}
