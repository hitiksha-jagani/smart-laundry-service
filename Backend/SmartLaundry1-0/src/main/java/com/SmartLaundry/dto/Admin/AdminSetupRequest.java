package com.SmartLaundry.dto.Admin;

import lombok.Data;

@Data
public class AdminSetupRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private boolean isBlocked;
}

