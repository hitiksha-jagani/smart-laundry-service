package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.RequestProfileDTO;
import com.SmartLaundry.dto.ServiceProviderProfileDTO;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.AddressRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Admin.RequestService;
import com.SmartLaundry.service.DeliveryAgent.DeliveryAgentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("")
public class RequestController {

    @Autowired
    private RequestService requestService;

    private final RedisTemplate<String, Object> redisTemplate;

    public RequestController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/provider-requests
    // Return all the pending requests of service provider.
    @GetMapping("/provider-requests")
    public ResponseEntity<List<ServiceProviderRequestDTO>> getAllPendingProvidersProfiles() {
        List<ServiceProviderRequestDTO> profiles = requestService.getAllProviderProfiles();
        return ResponseEntity.ok(profiles);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/accept-provider/{userId}
    // Accpet service provider request.
    @PostMapping("/accept-provider/{userId}")
    public ResponseEntity<String> acceptProviderRequest(@PathVariable String userId){
        try {
            String message = requestService.acceptProvider(userId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/reject-provider/{userId}
    // Reject service provider request.
    @PostMapping("/reject-provider/{userId}")
    public ResponseEntity<String> rejectProviderRequest(@PathVariable String userId){
        try {
            String message = requestService.rejectProvider(userId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/agent-requests
    // Return all the pending requests of delivery agent.
    @GetMapping("/agent-requests")
    public ResponseEntity<List<RequestProfileDTO>> getAllPendingAgentsProfiles() {
        List<RequestProfileDTO> profiles = requestService.getAllAgentProfiles();
        return ResponseEntity.ok(profiles);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/accept-agent/{userId}
    // Accpet delivery agent request.
    @PostMapping("/accept-agent/{userId}")
    public ResponseEntity<String> acceptAgentRequest(@PathVariable String userId){
        try {
            String message = requestService.acceptAgent(userId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/reject-agent/{userId}
    // Reject delivery agent request.
    @PostMapping("/reject-agent/{userId}")
    public ResponseEntity<String> rejectAgentRequest(@PathVariable String userId){
        try {
            String message = requestService.rejectAgent(userId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }

}
