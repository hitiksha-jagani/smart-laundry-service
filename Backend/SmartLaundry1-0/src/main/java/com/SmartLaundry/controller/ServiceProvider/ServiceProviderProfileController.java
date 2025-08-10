package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ChangePasswordRequestDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.ChangePasswordService;
import com.SmartLaundry.service.JWTService;
import com.SmartLaundry.service.ServiceProvider.ServiceProviderProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/sp")
public class ServiceProviderProfileController {

    @Autowired
    private ServiceProviderProfileService serviceProviderProfileService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ChangePasswordService changePasswordService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    private void checkIfBlocked(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isBlocked()) {
            throw new RuntimeException("Your account is blocked by admin. You cannot perform this action.");
        }
    }


    // http://localhost:8080/sp/complete-sp-profile/{userId}
    // Render a form for service provider to submit their profile.
//    @PostMapping(value ="/complete-sp-profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> completeServiceProviderProfile(
//            @PathVariable String userId,
//            @RequestPart("data") @Valid String data,
//            @RequestPart("aadharCard") MultipartFile aadharCard,
//            @RequestPart(value = "panCard", required = false) MultipartFile panCard,
//            @RequestPart("utilityBill") MultipartFile utilityBill,
//            @RequestPart("profilePhoto") MultipartFile profilePhoto
//    ) throws IOException {
//        checkIfBlocked(userId);
//        ServiceProviderRequestDTO dto = objectMapper.readValue(data, ServiceProviderRequestDTO.class);
//        return ResponseEntity.ok(serviceProviderProfileService.completeServiceProviderProfile(userId, dto, aadharCard, panCard, utilityBill, profilePhoto));
//    }
    @PostMapping(value ="/complete-sp-profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> completeServiceProviderProfile(
            @PathVariable String userId,
            @RequestPart("data") String data,
            @RequestPart("aadharCard") MultipartFile aadharCard,
            @RequestPart(value = "panCard", required = false) MultipartFile panCard,
            @RequestPart("utilityBill") MultipartFile utilityBill,
            @RequestPart("profilePhoto") MultipartFile profilePhoto
    ) {
        try {
            checkIfBlocked(userId);
            ServiceProviderRequestDTO dto = objectMapper.readValue(data, ServiceProviderRequestDTO.class);
            String response = serviceProviderProfileService.completeServiceProviderProfile(userId, dto, aadharCard, panCard, utilityBill, profilePhoto);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid JSON format for profile data.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error occurred.");
        }
    }


    // http://localhost:8080/sp-profile/change-password
    // Change password for service provider
    @PutMapping("/change-password")
    public ResponseEntity<String> changeServiceProviderPassword(
            HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO
    ) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        checkIfBlocked(userId);
        return ResponseEntity.ok(changePasswordService.changePassword(userId, changePasswordRequestDTO));
    }
    // Fetch service provider profile
    @GetMapping("/sp-profile")
    public ResponseEntity<ServiceProviderProfileDTO> getServiceProviderProfile(HttpServletRequest request) {
        String userId = (String)jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceProviderProfileService.getServiceProviderProfileDetail(userId));
    }

    @GetMapping("{userId}")
    public ResponseEntity<String> test(@PathVariable String userId) {
        return ResponseEntity.ok("You hit " + userId);
    }

}
