package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.*;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.GeocodingConfigRepository;
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

    @Autowired
    private GeocodingConfigRepository geocodingConfigRepository;

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/providers
    // Get all api providers
    @GetMapping("/geo-api/providers")
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
    // http://localhost:8080/configurations/geo-api
    // Save api key
    @PostMapping("/geo-api")
    public ResponseEntity<String> saveSetting(@RequestBody @Valid GeocodingConfig geocodingConfig,
                                              HttpServletRequest request) throws AccessDeniedException {

        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);

        settingService.saveConfig(geocodingConfig, user);
        return ResponseEntity.ok("Configuration saved successfully");
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/geo-api/history
    // Fatch past api key
    @GetMapping("/geo-api/history")
    public ResponseEntity<List<GeocodingConfig>> getAllConfigs(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);

        List<GeocodingConfig> configs = settingService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/geo-api/history/{id}
    // Change status of geocoding api
    @PutMapping("/geo-api/history/{id}")
    public ResponseEntity<String> changeGeocodingStatus(HttpServletRequest request, @PathVariable Long id) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.changeGeoCodingStatus(id));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/revenue-breakdown
    // Set revenue breakdown
    @PostMapping("/revenue-breakdown")
    public ResponseEntity<RevenueSettingResponseDTO> setRevenue(HttpServletRequest request, @RequestBody @Valid RevenueSettingRequestDTO revenueSettingRequestDTO) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.setRevenue(revenueSettingRequestDTO));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/revenue-breakdown/history
    // Get revenue breakdown
    @GetMapping("/revenue-breakdown/history")
    public ResponseEntity<List<RevenueBreakDown>> getRevenue(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.getRevenue());
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/configurations/revenue-breakdown/history/{id}
    // Change status of revenue breakdown
    @PutMapping("/revenue-breakdown/history/{id}")
    public ResponseEntity<String> changeRevenueBreakdownStatus(HttpServletRequest request, @PathVariable Long id) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(userId);
        roleCheckingService.isAdmin(user);
        return ResponseEntity.ok(settingService.changeRevenueBreakdownStatus(id));
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

}
