package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.ServiceProvider.ProviderMyProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.SmartLaundry.service.JWTService;
import java.time.LocalDate;
import java.util.List;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderProfileService;
@RestController
@RequestMapping("sp")
@RequiredArgsConstructor
public class ProviderMyProfileController {
    @Autowired
    private final JWTService jwtService;
    @Autowired
    private final ProviderMyProfileService availabilityService;
    @Autowired
    private final UserRepository usersRepository;

    @PostMapping("/block-days")
    public ResponseEntity<?> blockDays(
            @RequestParam String providerId,
            @RequestBody List<LocalDate> dates
    ) {
        Users user = usersRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isBlocked()) {
            return ResponseEntity.status(403).body("Your account is blocked by admin. You cannot perform this action.");
        }

        availabilityService.markBlockOffDays(providerId, dates);
        return ResponseEntity.ok("Block-off days updated");
    }


    @GetMapping("/block-days")
    public ResponseEntity<List<LocalDate>> getBlockedDays(@RequestParam String providerId) {
        return ResponseEntity.ok(availabilityService.getBlockOffDays(providerId));
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editServiceProviderProfile(
            HttpServletRequest request,
            @RequestBody ServiceProviderProfileDTO profileDTO
    ) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isBlocked()) {
            return ResponseEntity.status(403).body("Your account is blocked by admin. You cannot perform this action.");
        }

        return ResponseEntity.ok(availabilityService.editServiceProviderDetail(userId, profileDTO));
    }

}

