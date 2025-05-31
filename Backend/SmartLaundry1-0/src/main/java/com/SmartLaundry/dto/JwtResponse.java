package com.SmartLaundry.dto;

import com.SmartLaundry.model.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {

    private String jwtToken;
    private String username;
    private String role;
}
