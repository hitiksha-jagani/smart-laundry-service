package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderDto;
import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.ServiceProvider.ProviderMyProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.SmartLaundry.service.JWTService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderProfileService;
import org.springframework.web.multipart.MultipartFile;

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

    private String getUserIdFromRequest(HttpServletRequest request) {
        String token = JWTService.extractTokenFromHeader(request);
        return jwtService.extractUserId(token).toString();
    }
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

    @PutMapping("/sp-profile/edit")
    @Transactional
    public ResponseEntity<String> editServiceProviderProfile(
            @RequestParam("profile") String profileJson,
            @RequestParam(value = "aadharCard", required = false) MultipartFile aadharCard,
            @RequestParam(value = "panCard", required = false) MultipartFile panCard,
            @RequestParam(value = "utilityBill", required = false) MultipartFile utilityBill,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            HttpServletRequest request
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ServiceProviderProfileDTO profileDTO = mapper.readValue(profileJson, ServiceProviderProfileDTO.class);

        String userId = getUserIdFromRequest(request);

        String result = availabilityService.editServiceProviderDetail(userId, profileDTO, aadharCard, panCard, utilityBill, profilePhoto);

        return ResponseEntity.ok(result);
    }

}

