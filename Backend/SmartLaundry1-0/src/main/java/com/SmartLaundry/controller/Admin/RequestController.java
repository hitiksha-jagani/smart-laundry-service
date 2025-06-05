package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.DeliveryAgent.RequestProfileDTO;
import com.SmartLaundry.dto.ServiceProviderProfileDTO;
import com.SmartLaundry.service.Admin.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    public ResponseEntity<List<ServiceProviderProfileDTO>> getAllPendingProvidersProfiles() {

        Set<String> keys = redisTemplate.keys("serviceProviderProfile:*");

        if (keys == null || keys.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<ServiceProviderProfileDTO> pendingProfiles = new ArrayList<>();
        for (String key : keys) {
            ServiceProviderProfileDTO dto = (ServiceProviderProfileDTO) redisTemplate.opsForValue().get(key);
            if (dto != null) {
                pendingProfiles.add(dto);
            }
        }

        return ResponseEntity.ok(pendingProfiles);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/provider-accept/{userId}
    // Accpet service provider request.
    @Transactional
    @PostMapping("/provider-accept/{userId}")
    public ResponseEntity<String> acceptProviderRequest(@PathVariable String userId){
        return ResponseEntity.ok(requestService.acceptProvider(userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/provider-reject/{userId}
    // Reject service provider request.
    @Transactional
    @PostMapping("/provider-reject/{userId}")
    public ResponseEntity<String> rejectProviderRequest(@PathVariable String userId){
        return ResponseEntity.ok(requestService.rejectProvider(userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/agent-requests
    // Return all the pending requests of delivery agent.
    @GetMapping("/agent-requests")
    public ResponseEntity<List<RequestProfileDTO>> getAllPendingAgentsProfiles() {

        Set<String> keys = redisTemplate.keys("DeliveryAgentProfile:*");

        if (keys == null || keys.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<RequestProfileDTO> pendingProfiles = new ArrayList<>();
        for (String key : keys) {
            RequestProfileDTO dto = (RequestProfileDTO) redisTemplate.opsForValue().get(key);
            if (dto != null) {
                pendingProfiles.add(dto);
            }
        }

        return ResponseEntity.ok(pendingProfiles);
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/agent-accept/{userId}
    // Accpet delivery agent request.
    @Transactional
    @PostMapping("/agent-accept/{userId}")
    public ResponseEntity<String> acceptAgentRequest(@PathVariable String userId){
        return ResponseEntity.ok(requestService.acceptAgent(userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/agent-reject/{userId}
    // Reject delivery agent request.
    @Transactional
    @PostMapping("/agent-reject/{userId}")
    public ResponseEntity<String> rejectAgentRequest(@PathVariable String userId){
        return ResponseEntity.ok(requestService.rejectAgent(userId));
    }

}
