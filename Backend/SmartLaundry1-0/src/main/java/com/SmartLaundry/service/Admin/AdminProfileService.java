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
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.CustomUserDetailsService;
import com.SmartLaundry.util.UsernameUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private  UsernameUtil usernameUtil;

    //@author Hitiksha Jagani
    // Logic to fetch profile details
    @Cacheable(value = "adminProfileCache", key = "#userId")
    public AdminProfileResponseDTO getProfileDetail(String userId) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        UserAddress address = addressRepository.findByUsers(user)
                .orElse(null);

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
    public void editProfile(AdminEditProfileRequestDTO request, String username) throws AccessDeniedException {

        Optional<Users> userDetail;

        if(usernameUtil.isEmail(username)){
            userDetail = userRepository.findByEmail(username);
        } else {
            userDetail = userRepository.findByPhoneNo(username);
        }

        Users user = userDetail.orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        AdminEditProfileRequestDTO.AddressDTO addr = request.getAddresses();
        if (addr != null) {
            if (addr.getPincode() != null && !addr.getPincode().matches("^[0-9]{6}$")) {
                throw new ExceptionMsg("Pincode must be 6 digits.");
            }

            City city = null;
            if (addr.getCityName() != null) {
                city = cityRepository.findByCityName(addr.getCityName())
                        .orElseThrow(() -> new RuntimeException("Invalid city: " + addr.getCityName()));
            }

            UserAddress addresses = user.getAddress();

//            UserAddress address;
//            if (addresses != null && !addresses.isEmpty()) {
//                address = addresses.getFirst(); // get the first address
//            } else {
//                address = new UserAddress();
//            }


//            if (addr.getName() != null) address.setName(addr.getName());
//            if (addr.getAreaName() != null) address.setAreaName(addr.getAreaName());
//            if (addr.getPincode() != null) address.setPincode(addr.getPincode());
//            if (city != null) address.setCity(city);
//
//            user.setAddress(List.of(address));
        }

        userRepository.save(user);
    }

}
