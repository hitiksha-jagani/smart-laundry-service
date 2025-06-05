package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.controller.ExtractHeader;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.exception.ExceptionMsg;
import com.SmartLaundry.exception.ForbiddenAccessException;
import com.SmartLaundry.exception.FormatException;
import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.DeliveryAgent.DeliveryAgentProfileService;
import com.SmartLaundry.service.JWTService;
import com.SmartLaundry.util.UsernameUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("")
public class DeliveryAgentProfileController {

    @Autowired
    private DeliveryAgentProfileService deliveryAgentProfileService;

    @Autowired
    private ExtractHeader extractHeader;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UsernameUtil usernameUtil;

    @Autowired
    private UserRepository userRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public DeliveryAgentProfileController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/complete-agent-profile/{userId}
    // Render a form for delivery agent to accept details of agent.
    @PostMapping("/complete-agent-profile/{userId}")
    public ResponseEntity<String> completeProfile(
            @Valid @RequestBody DeliveryAgentCompleteProfileRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String userId) throws AccessDeniedException {

        // Validation
        if(request.getVehicleNumber() == null || request.getVehicleNumber().isBlank()){
            throw new ExceptionMsg("Vehicle number is required.");
        }

        if(!request.getAccountHolderName().matches("^[A-Za-z\\s]+$")){
            throw new FormatException("Account holder name contains invalid characters.");
        }

        if(!request.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")){
            throw new FormatException("Invalid IFSC Code format.");
        }

        // Set null if user does not provide pancard
        if (request.getPanCardPhoto() != null && (request.getPanCardPhoto().length == 0)) {
            request.setPanCardPhoto(null);
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        
        if (!UserRole.DELIVERY_AGENT.equals(user.getRole())) {
            throw new ForbiddenAccessException("You are not applicable for this page.");
        }

        // Save profile data in Redis with key prefix + userId
        redisTemplate.opsForValue().set("DeliveryAgentProfile:" + userId, request);

        return ResponseEntity.ok("Your request is send successfully. Wait for a response. ");
    }

}
