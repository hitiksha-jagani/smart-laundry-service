package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.config.SetupChecker;
import com.SmartLaundry.dto.Admin.AdminSetupRequest;
import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Admin.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminSetupController {

    @Autowired
    private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AdminSetupController.class);

    @PostMapping("/setup-admin")
    public ResponseEntity<?> createAdmin(@RequestBody AdminSetupRequest request) {
        if (!SetupChecker.adminSetupRequired) {
            log.info("Admin setup already done.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin setup already done.");
        }

        Users admin = new Users();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPhoneNo(request.getPhone());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setBlocked(false);
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);

        SetupChecker.adminSetupRequired = false;
        return ResponseEntity.ok("Admin created successfully. You can now log in.");
    }

    @GetMapping("/setup-admin")
    public ResponseEntity<Map<String, Object>> checkAdminSetup() {
        boolean adminExists = userRepository.existsByRole(UserRole.ADMIN);

        Map<String, Object> response = new HashMap<>();
        response.put("adminSetupRequired", !adminExists);
        return ResponseEntity.ok(response);
    }

}
