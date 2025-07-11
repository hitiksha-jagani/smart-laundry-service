package com.SmartLaundry.service;

import com.SmartLaundry.dto.ChangePasswordRequestDTO;
import com.SmartLaundry.exception.PasswordMisMatchException;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String changePassword(String userId, ChangePasswordRequestDTO changePasswordRequestDTO) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Check whether old password match with stored password or not.
        if (!passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match.");
        }

        // Check if new password not match with old password
        if (changePasswordRequestDTO.getOldPassword().equals(changePasswordRequestDTO.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from old password.");
        }


        // Check password and confirm password
        if(!changePasswordRequestDTO.getNewPassword().equals(changePasswordRequestDTO.getConfirmPassword())){
            throw new PasswordMisMatchException();
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));

        userRepository.save(user);

        return "Password changed successfully.";
    }
}
