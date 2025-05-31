package com.SmartLaundry.dto;

import com.SmartLaundry.model.UserRole;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
public class RegistrationRequestDTO {

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    private String firstName;

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters.")
    private String lastName;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    @NotNull(message = "Role is required.")
    private UserRole role;

    @Valid
    private AddressDTO addresses;

    @Data
    public static class AddressDTO{
        @NotBlank(message = "Name is required.")
        private String name;

        @NotBlank(message = "Area name is required.")
        private String areaName;

        @NotBlank(message = "Pincode is required.")
        private String pincode;

        @NotNull(message = "City ID is required.")
        private Long cityId;
    }

}
