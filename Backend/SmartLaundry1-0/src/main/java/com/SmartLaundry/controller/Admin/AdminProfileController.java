package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.AdminEditProfileRequestDTO;
import com.SmartLaundry.dto.Admin.AdminProfileResponseDTO;
import com.SmartLaundry.dto.ChangePasswordRequestDTO;
import com.SmartLaundry.service.Admin.AdminProfileService;
import com.SmartLaundry.service.ChangePasswordService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("")
public class AdminProfileController {

    @Autowired
    private AdminProfileService adminProfileService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ChangePasswordService changePasswordService;

    //@author Hitiksha Jagani
    // http://localhost:8080/admin-profile
    // Return profile detail of the admin.
    @GetMapping("/admin-profile")
    public ResponseEntity<AdminProfileResponseDTO> getAdminDetail(HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(adminProfileService.getProfileDetail(userId));
    }

    //@author Hitiksha Jagani
    // http://localhost:8080/admin-profile/edit
    // Render form for edit profile details.
    @PutMapping("/admin-profile/edit")
    public ResponseEntity<String> editProfile(@RequestBody AdminEditProfileRequestDTO adminEditProfileRequestDTO,
                                              HttpServletRequest request) throws AccessDeniedException {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(adminProfileService.editProfile(adminEditProfileRequestDTO, userId));
    }

    // @author Hitiksha Jagani
    // http://localhost:8080/admin-profile/change-password
    // Change password
    @PutMapping("/admin-profile/change-password")
    public ResponseEntity<String> changeAdminPassword(HttpServletRequest request, @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO){
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(changePasswordService.changePassword(userId, changePasswordRequestDTO));
    }

}
