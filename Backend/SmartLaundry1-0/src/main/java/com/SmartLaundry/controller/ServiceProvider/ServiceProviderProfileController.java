package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ChangePasswordRequestDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
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


    // http://localhost:8080/complete-sp-profile/{userId}
    // Render a form for service provider to submit their profile.
    @PostMapping(value ="{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> completeServiceProviderProfile(
            @PathVariable String userId,
            @RequestPart("data") @Valid String data,
            @RequestPart("aadharCard") MultipartFile aadharCard,
            @RequestPart(value = "panCard", required = false) MultipartFile panCard,
            @RequestPart("utilityBill") MultipartFile utilityBill,
            @RequestPart("profilePhoto") MultipartFile profilePhoto
    ) throws IOException {
        ServiceProviderRequestDTO dto = objectMapper.readValue(data, ServiceProviderRequestDTO.class);
        return ResponseEntity.ok(serviceProviderProfileService.completeServiceProviderProfile(userId, dto, aadharCard, panCard, utilityBill, profilePhoto));
    }

    // http://localhost:8080/sp-profile
    // Return profile detail page of service provider.
    @GetMapping("/sp-profile")
    public ResponseEntity<ServiceProviderProfileDTO> getServiceProviderProfile(HttpServletRequest request) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        return ResponseEntity.ok(serviceProviderProfileService.getServiceProviderProfileDetail(userId));
    }

    // http://localhost:8080/sp-profile/edit
    // Modify existing service provider profile.
//    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> editServiceProviderProfile(
//            HttpServletRequest request,
//            @ModelAttribute ServiceProviderProfileDTO profileDTO
//    ) {
//        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
//        return ResponseEntity.ok(serviceProviderProfileService.editServiceProviderDetail(userId, profileDTO));
//    }
    @PutMapping("/edit")
    public ResponseEntity<String> editServiceProviderProfile(
            HttpServletRequest request,
            @RequestBody ServiceProviderProfileDTO profileDTO
    ){
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(serviceProviderProfileService.editServiceProviderDetail(userId, profileDTO));
    }


    // http://localhost:8080/sp-profile/change-password
    // Change password for service provider
    @PutMapping("/change-password")
    public ResponseEntity<String> changeServiceProviderPassword(
            HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO
    ) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));

        return ResponseEntity.ok(changePasswordService.changePassword(userId, changePasswordRequestDTO));
    }
    @GetMapping("{userId}")
    public ResponseEntity<String> test(@PathVariable String userId) {
        return ResponseEntity.ok("You hit " + userId);
    }

}
