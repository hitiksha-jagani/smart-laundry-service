package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.Admin.ComplaintCategoryResponseDTO;
import com.SmartLaundry.dto.Admin.ComplaintCategorySetDTO;
import com.SmartLaundry.service.Admin.SettingService;
import com.SmartLaundry.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class SettingController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SettingService settingService;

    // @author Hitiksha Jagani
    // http://localhost:8080/complaint-category
    // Set complaint category
    @PostMapping("/complaint-category")
    public ResponseEntity<ComplaintCategoryResponseDTO> setComplaintCategory(
            HttpServletRequest request,
            @Valid @RequestBody ComplaintCategorySetDTO complaintCategorySetDTO) {
        String userId = (String) jwtService.extractUserId(jwtService.extractTokenFromHeader(request));
        return ResponseEntity.ok(settingService.setComplaintCategory(userId, complaintCategorySetDTO));
    }
}
