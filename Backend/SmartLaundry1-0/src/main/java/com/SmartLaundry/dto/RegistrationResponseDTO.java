package com.SmartLaundry.dto;
import lombok.Data;

@Data
public class RegistrationResponseDTO {
    private String phone;
    private String email;
    private String message;

    //Constructor
    public RegistrationResponseDTO(String phone, String email, String message) {
        this.phone = phone;
        this.email = email;
        this.message = message;
    }
}