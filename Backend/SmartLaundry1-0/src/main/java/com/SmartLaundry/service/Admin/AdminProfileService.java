package com.SmartLaundry.service.Admin;

import com.SmartLaundry.config.RedisConfig;
import com.SmartLaundry.dto.Admin.AdminEditProfileRequestDTO;
import com.SmartLaundry.dto.Admin.AdminProfileResponseDTO;
import com.SmartLaundry.exception.ExceptionMsg;
import com.SmartLaundry.model.City;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.AddressRepository;
import com.SmartLaundry.repository.CityRepository;
import com.SmartLaundry.repository.UserAddressRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.CustomUserDetailsService;
import com.SmartLaundry.util.UsernameUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private  UsernameUtil usernameUtil;

    @Autowired
    private RoleCheckingService roleCheckingService;

    //@author Hitiksha Jagani
    // Logic to fetch profile details
//    @Cacheable(value = "adminProfileCache", key = "#userId")
    @Transactional
    public AdminProfileResponseDTO getProfileDetail(String userId) throws AccessDeniedException {

        Users user = roleCheckingService.checkUser(userId);

        roleCheckingService.isAdmin(user);

        UserAddress address = roleCheckingService.checkUserAddress(user);

        AdminProfileResponseDTO.AddressDTO addressDTO = null;
        if (address != null) {
            addressDTO = AdminProfileResponseDTO.AddressDTO.builder()
                    .name(address.getName())
                    .areaName(address.getAreaName())
                    .pincode(address.getPincode())
                    .cityName(address.getCity().getCityName())
                    .build();
        }

        return AdminProfileResponseDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhoneNo())
                .email(user.getEmail())
                .addresses(addressDTO)
                .build();
    }

    //@author Hitiksha Jagani
    // Logic to store edited profile details
    @Transactional
    public String editProfile(AdminEditProfileRequestDTO request, String userId) throws AccessDeniedException {
        System.out.println("Incoming userId: " + userId);
        System.out.println("Incoming request: " + request);

        Users user = roleCheckingService.checkUser(userId);

        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        AdminEditProfileRequestDTO.AddressDTO addr = request.getAddresses();
        if (addr != null) {
            // Validate pincode
            if (addr.getPincode() != null && !addr.getPincode().trim().isEmpty()
                    && !addr.getPincode().matches("^[0-9]{6}$")) {
                throw new ExceptionMsg("Pincode must be 6 digits.");
            }

            // Get city if provided
            City city = null;
            System.out.println("City : " + addr.getCityName());
            if (addr.getCityName() != null && !addr.getCityName().trim().isEmpty()) {
                city = cityRepository.findByCityName(addr.getCityName())
                        .orElseThrow(() -> new RuntimeException("Invalid city: " + addr.getCityName()));
            }

            // Find existing address
            UserAddress existingAddress = userAddressRepository.findByUsers(user);

            if (existingAddress == null) {
                // Create new address
                UserAddress newAddress = new UserAddress();
                newAddress.setUsers(user);
                newAddress.setCity(city);
                newAddress.setName(addr.getName());
                newAddress.setAreaName(addr.getAreaName());
                newAddress.setPincode(addr.getPincode());
                userAddressRepository.save(newAddress);
            } else {
                // Update existing address
                if (city != null) existingAddress.setCity(city);
                if (addr.getName() != null && !addr.getName().trim().isEmpty()) {
                    existingAddress.setName(addr.getName());
                }
                if (addr.getAreaName() != null && !addr.getAreaName().trim().isEmpty()) {
                    existingAddress.setAreaName(addr.getAreaName());
                }
                if (addr.getPincode() != null && !addr.getPincode().trim().isEmpty()) {
                    existingAddress.setPincode(addr.getPincode());
                }

                userAddressRepository.save(existingAddress);
            }
        }

        userRepository.save(user);
        return "Profile updated successfully.";
    }

}
