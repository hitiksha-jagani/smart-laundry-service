package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.LocationUpdateDTO;
import com.SmartLaundry.service.DeliveryAgent.DeliveryAgentService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
public class DeliveryAgentController {

    @Autowired
    private DeliveryAgentService deliveryAgentService;

    @Autowired
    private JWTService jwtService;

    // @author Hitiksha Jagani
    // http://localhost:8080/delivery-agent/location
    // Store latest location of delivery agent automatically frequently every 5-10 seconds and store data from redis to db in every 5 minute.
    @PutMapping("/delivery-agent/location")
    public ResponseEntity<?> updateLocation(
            @RequestBody LocationUpdateDTO locationDTO,
            HttpServletRequest request) {

        String agentId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        deliveryAgentService.updateLocation(agentId, locationDTO.getLatitude(), locationDTO.getLongitude());

        return ResponseEntity.ok().build();
    }

}

