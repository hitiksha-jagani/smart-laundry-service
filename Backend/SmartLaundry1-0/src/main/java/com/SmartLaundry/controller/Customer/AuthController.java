package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.JwtRequest;
import com.SmartLaundry.dto.JwtResponse;
import com.SmartLaundry.dto.RegistrationRequestDTO;
import com.SmartLaundry.dto.RegistrationResponseDTO;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Customer.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //@author HitikshaJagani
    // http://localhost:8080/register
    // Render registration form.
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO request){
        return ResponseEntity.ok(authService.registerUser(request));
    }

    //@author HitikshaJagani
    // http:/localhost:8080/login
    // Render login form.
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest request){
        JwtResponse response = authService.loginUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
