package com.SmartLaundry.dto.Customer;
import lombok.Data;

@Data
public class UserUpdateDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
    private String preferredLanguage;
}

