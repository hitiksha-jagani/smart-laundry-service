package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.LocationUpdateDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import com.SmartLaundry.service.DeliveryAgent.DeliveryAgentService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestController
@RequestMapping("")
public class DeliveryAgentController {

    @Autowired
    private DeliveryAgentService deliveryAgentService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleCheckingService roleCheckingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/delivery-agent/update-location
    // Store latest location of delivery agent automatically frequently every 5-10 seconds and store data from redis to db in every 5 minute.
    @PutMapping("/delivery-agent/update-location")
    public ResponseEntity<?> updateLocation(
            @RequestBody LocationUpdateDTO locationDTO,
            HttpServletRequest request) throws AccessDeniedException {

        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(agentId);
        roleCheckingService.isDeliveryAgent(user);
        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }

        deliveryAgentService.updateLocation(agentId, locationDTO.getLatitude(), locationDTO.getLongitude());

        return ResponseEntity.ok().build();
    }

    // http://localhost:8080/delivery-agent/get-location
    @GetMapping("/delivery-agent/get-location")
    public ResponseEntity<?> getAgentLocation(HttpServletRequest request) throws AccessDeniedException {
        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        Users user = roleCheckingService.checkUser(agentId);
        roleCheckingService.isDeliveryAgent(user);
        Map<String, Double> location = deliveryAgentService.getCurrentLocation(agentId);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Location not available or expired for agent: " + agentId);
        }
    }


}

