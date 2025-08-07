package com.SmartLaundry.controller.Customer;
import com.SmartLaundry.dto.Customer.UserUpdateDto;
import com.SmartLaundry.dto.LanguageUpdateDto;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Customer.CustomerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/customer/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    @Autowired
    private final CustomerProfileService customerProfileService;
    @Autowired
    private final UserRepository usersRepository;

    @PutMapping("/set-language")
    public ResponseEntity<String> updateLanguage(@RequestBody LanguageUpdateDto dto) throws AccessDeniedException {

        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }
        user.setPreferredLanguage(dto.getLanguage());
        usersRepository.save(user);

        return ResponseEntity.ok("Preferred language updated to " + dto.getLanguage());
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody UserUpdateDto dto) throws AccessDeniedException {
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isBlocked()) {
            throw new AccessDeniedException("Your account is blocked by admin. You cannot perform this action.");
        }
        customerProfileService.updateUserProfile(dto);
        return ResponseEntity.ok("User profile updated successfully.");
    }
}

