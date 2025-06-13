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
        if (!request.getPhone().matches("^\\+?[0-9]{10,15}$")) {
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
        double[] latLng;
        try {
            latLng = geoUtils.getLatLng(fullAddress);
        } catch (Exception e) {
            throw new RuntimeException("Geo location service failed: " + e.getMessage(), e);
        }

        // Set latitude & longitude if found
        try {
        if (latLng[0] != 0.0 || latLng[1] != 0.0) {
            address.setLatitude(latLng[0]);
            address.setLongitude(latLng[1]);
        } } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Save data
        users.setAddress(address);

        try {
            userRepository.save(users);
            System.out.println("User saved successfully");
        } catch (Exception e) {
            e.printStackTrace(); // VERY important
            throw e;
        }


        return new RegistrationResponseDTO(users.getPhoneNo(), users.getEmail(), "Successfully Registered");
    }


    //@author Hitiksha Jagani
    // Logic for login
    public JwtResponse loginUser(JwtRequest request) {
        String token;
        Optional<Users> users;

        Authentication authentication = null;
        try {
            authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password!");
        }

        if(request.getUsername().matches("^\\+?[0-9]{10,15}$")){
            users = userRepository.findByPhoneNo(request.getUsername());
        } else {
            users = userRepository.findByEmail(request.getUsername());
        }

        if(authentication.isAuthenticated()) {
            token = jwtService.generateToken(users.get().getUserId(), request.getUsername());

            return JwtResponse.builder()
                    .jwtToken(token)
                    .username(request.getUsername())
                    .role(users.get().getRole().toString())
                    .build();
        }

        throw new RuntimeException();
    }

}