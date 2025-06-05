package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.controller.ExtractHeader;
import com.SmartLaundry.dto.Admin.AdminEditProfileRequestDTO;
import com.SmartLaundry.dto.Admin.AdminProfileResponseDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.AdminProfileService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.angus.mail.iap.ResponseInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("")
public class AdminProfileController {

    @Autowired
    private ExtractHeader extractHeader;

    private final AdminProfileService adminProfileService;

    private final JWTService jwtService;

    public AdminProfileController(AdminProfileService adminProfileService, JWTService jwtService) {
        this.adminProfileService = adminProfileService;
        this.jwtService = jwtService;
    }

    //@author HitikshaJagani
    // http://localhost:8080/admin-profile
    // Return profile detail of the admin.
    @GetMapping("/admin-profile")
    public ResponseEntity<AdminProfileResponseDTO> getAdminDetail(HttpServletRequest request) throws AccessDeniedException {
        String token = extractHeader.extractTokenFromHeader(request);
        String userId = (String)jwtService.extractUserId(token);
        return ResponseEntity.ok(adminProfileService.getProfileDetail(userId));
    }

    //@author HitikshaJagani
    // http://localhost:8080/admin-profile/edit
    // Render form for edit profile details.
    @PutMapping("/admin-profile/edit")
    public ResponseEntity<String> editProfile(@RequestBody AdminEditProfileRequestDTO request,
                                              @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {
        adminProfileService.editProfile(request, userDetails.getUsername());
        return ResponseEntity.ok("Profile updated successfully");
    }

}
