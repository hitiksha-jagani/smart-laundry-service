package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentProfileDTO;
//import com.SmartLaundry.repository.DeliveryAgentImageRepository;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.DeliveryAgent.DeliveryAgentProfileService;
import com.SmartLaundry.service.JWTService;
import com.SmartLaundry.util.UsernameUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("")
public class DeliveryAgentProfileController {

    @Autowired
    private DeliveryAgentProfileService deliveryAgentProfileService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UsernameUtil usernameUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

//    @Autowired
//    private DeliveryAgentImageRepository deliveryAgentImageRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // @author Hitiksha Jagani
    // http://localhost:8080/complete-agent-profile/{userId}
    // Render a form for delivery agent to accept details of agent.
    @PostMapping(value = "/complete-agent-profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> completeDeliveryAgentProfile(
            @PathVariable String userId,
            @RequestPart("data") @Valid String  data,
            @RequestPart("aadharCard") MultipartFile aadharCard,
            @RequestPart(value = "panCard", required = false) MultipartFile panCard,
            @RequestPart("drivingLicense") MultipartFile drivingLicense,
            @RequestPart("profilePhoto") MultipartFile profilePhoto
    ) throws IOException {
        // Parse JSON string to DTO
        ObjectMapper mapper = new ObjectMapper();
        DeliveryAgentCompleteProfileRequestDTO dataJson = mapper.readValue(data, DeliveryAgentCompleteProfileRequestDTO.class);
        return ResponseEntity.ok(deliveryAgentProfileService.completeProfile(userId, dataJson, aadharCard, panCard, drivingLicense, profilePhoto));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/agent-profile
    // Return a profile detail page of delivery agent.
    @GetMapping("/agent-profile")
    public ResponseEntity<DeliveryAgentProfileDTO> getDeliveryAgentDetail(HttpServletRequest request){
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(deliveryAgentProfileService.getProfileDetail(userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/agent-profile/edit
    // Modify existing details.
    @PutMapping("/agent-profile/edit")
    public ResponseEntity<String> editDeliveryAgentDetail(HttpServletRequest request, DeliveryAgentProfileDTO deliveryAgentProfileDTO){
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(deliveryAgentProfileService.editDetail(userId, deliveryAgentProfileDTO));
    }

}
