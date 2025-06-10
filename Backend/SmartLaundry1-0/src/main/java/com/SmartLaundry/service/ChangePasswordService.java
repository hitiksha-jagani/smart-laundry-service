package com.SmartLaundry.service;

import com.SmartLaundry.dto.ChangePasswordRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {
    public String changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {



        return "Password changed successfully.";
   }
}
