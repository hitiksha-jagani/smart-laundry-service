package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.JwtRequest;
import com.SmartLaundry.dto.JwtResponse;
import com.SmartLaundry.dto.RegistrationRequestDTO;
import com.SmartLaundry.dto.RegistrationResponseDTO;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Customer.AuthService;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.OTPService;
import com.SmartLaundry.service.Customer.SMSService;
import com.SmartLaundry.service.JWTService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AuthService authService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private SMSService smsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OTPService otpService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @author hitiksha-jagani
    // http://localhost:8080/register
    // Sign up
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO request){
        return ResponseEntity.ok(authService.registerUser(request));
    }

    // @author hitiksha-jagani
    // http://localhost:8080/login
    // Login
//    @PostMapping("/login")
//    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest request){
//        JwtResponse response = authService.loginUser(request);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }



    @PostMapping("/login")
    public String loginWithPassword(@RequestBody JwtRequest request) {
        return authService.loginUser(request); // sends OTP after verifying credentials
    }

    // Step 2: Verify OTP and return JWT token
    @PostMapping("/verify-otp")
    public ResponseEntity<JwtResponse> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        String normalizedKey;

        if (username.contains("@")) {
            normalizedKey = username.toLowerCase();
        } else {
            String digitsOnly = username.replaceAll("\\D", "");
            if (digitsOnly.length() > 10) {
                digitsOnly = digitsOnly.substring(digitsOnly.length() - 10);
            }
            normalizedKey = "+91" + digitsOnly;
        }

        boolean valid = otpService.validateOtp(normalizedKey, otp);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<Users> userOpt;
        if (username.contains("@")) {
            userOpt = userRepository.findByEmail(username.toLowerCase());
        } else {
            userOpt = userRepository.findByPhoneNo(username.replaceAll("\\D", "").substring(0, 10));
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Users user = userOpt.get();
        String token = jwtService.generateToken(user.getUserId(), username);
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(username)
                .role(user.getRole().toString())
                .build();
        return ResponseEntity.ok(response);
    }


}

