package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.JwtRequest;
import com.SmartLaundry.dto.JwtResponse;
import com.SmartLaundry.dto.RegistrationRequestDTO;
import com.SmartLaundry.dto.RegistrationResponseDTO;
import com.SmartLaundry.model.City;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.repository.AddressRepository;
import com.SmartLaundry.repository.CityRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.SmartLaundry.exception.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private GeoUtils geoUtils;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private  OTPService otpService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private EmailService emailService;

    //@author Hitiksha Jagani
    // Logic for registration
    // Store registration details in database.
    @Transactional
    public RegistrationResponseDTO registerUser(RegistrationRequestDTO request){

        // Validation
        if(!request.getFirstName().matches("^[A-Za-z\\s]+$")){
            throw new FormatException("First name");
        }
        if(!request.getLastName().matches("^[A-Za-z\\s]+$")){
            throw new FormatException("Last name");
        }
        if (!request.getPhone().matches("^[0-9]{10}$")) {
            throw new ExceptionMsg("Phone number must be 10 digits.");
        }

        // Set null if user does not provide email
        if (request.getEmail() != null && request.getEmail().isBlank()) {
            request.setEmail(null);
        }

        // Check if email or phone already exists
        if ((request.getEmail() != null && !request.getEmail().isBlank() &&
                userRepository.findByEmail(request.getEmail()).isPresent()) ||
                (request.getPhone() != null && userRepository.findByPhoneNo(request.getPhone()).isPresent())) {
            throw new UserAlreadyExistsException();
        }

        // Check password and confirm password
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new PasswordMisMatchException();
        }

        // Store user data in user object.
        Users users = new Users();
        users.setFirstName(request.getFirstName());
        users.setLastName(request.getLastName());
        users.setEmail(request.getEmail());
        users.setPhoneNo(request.getPhone());
        users.setRole(request.getRole());
        users.setPassword(passwordEncoder.encode(request.getPassword()));

        // Store address related data
        RegistrationRequestDTO.AddressDTO addr = request.getAddresses();

        // Address validation
        if (!addr.getPincode().matches("^[0-9]{6}$")) {
            throw new ExceptionMsg("Pincode must be 6 digits.");
        }

        City city = cityRepository.findById(addr.getCityId())
                .orElseThrow(() -> new RuntimeException("Invalid city ID: " + addr.getCityId()));

        UserAddress address = new UserAddress();
        address.setName(addr.getName());
        address.setAreaName(addr.getAreaName());
        address.setPincode(addr.getPincode());
        address.setCity(city);
        address.setUsers(users);

        // Build full address string for geocoding
        String fullAddress = String.format("%s, %s, %s, %s",
                addr.getName(),
                addr.getAreaName(),
                city.getCityName(),
                addr.getPincode());

        // Call utility to get coordinates
        double[] latLng = geoUtils.getLatLng(fullAddress);

        // Set latitude & longitude if found
        if (latLng[0] != 0.0 || latLng[1] != 0.0) {
            address.setLatitude(latLng[0]);
            address.setLongitude(latLng[1]);
        } else {
            System.out.println("⚠ Warning: Coordinates could not be determined.");
        }

        // Save data
        users.setAddress(address);
        userRepository.save(users);

        return new RegistrationResponseDTO(users.getPhoneNo(), users.getEmail(), "Successfully Registered");
    }

    public String loginUser(JwtRequest request) {
        String input = request.getUsername().trim();
        Optional<Users> users;

        // Step 1: Authenticate username/password
        try {
            manager.authenticate(
                    new UsernamePasswordAuthenticationToken(input, request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password!");
        }

        String normalizedKey;

        // Step 2: Normalize and fetch user
        if (input.matches("^\\+?[0-9]{10,15}$")) {
            String digitsOnly = input.replaceAll("\\D", ""); // Remove any non-digit
            if (digitsOnly.length() > 10) {
                digitsOnly = digitsOnly.substring(digitsOnly.length() - 10); // Get last 10 digits
            }
            normalizedKey = "+91" + digitsOnly; // For OTP key
            users = userRepository.findByPhoneNo(digitsOnly); // DB stores 10-digit only
        } else if (input.contains("@")) {
            normalizedKey = input.toLowerCase();
            users = userRepository.findByEmail(normalizedKey);
        } else {
            throw new BadCredentialsException("Invalid login identifier format");
        }

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + input);
        }

        Users user = users.get();

        // Step 3: Generate and store OTP
        String otp = otpService.generateOtp(normalizedKey);

        // Step 4: Send OTP
        if (normalizedKey.contains("@")) {
            emailService.sendOtp(user.getEmail(), otp);
            return "OTP sent to your email address. Please check and verify.";
        } else {
            smsService.sendOtp(user.getPhoneNo(), otp);
            return "OTP sent to your phone number. Please check and verify.";
        }
    }
}