package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.ItemRepository;
import com.SmartLaundry.repository.ServiceRepository;
import com.SmartLaundry.repository.SubServiceRepository;
import com.SmartLaundry.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@RestController
@RequestMapping("/service-provider/")
@RequiredArgsConstructor
public class ServiceProviderProfileController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    // http://localhost:8080/service-provider/submit-profile/{userId}
    // Return a form for service provider to complete profile details
    @PostMapping("submit-profile/{userId}")
    public ResponseEntity<String> submitProfile(
            @Valid
            @PathVariable String userId,
            @RequestBody ServiceProviderRequestDTO profileDTO) {

        // Check if is exist or not
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (PriceDTO priceDTO : profileDTO.getPriceDTO()) {

            Items items = itemRepository.findById(priceDTO.getItem().getItemId())
                    .orElseThrow(() -> new NoSuchElementException(priceDTO.getItem().getItemId() + " is not available."));
        }

        // Store null if not provided
        if (profileDTO.getPanCardPhoto() == null || profileDTO.getPanCardPhoto().isBlank()) {
            profileDTO.setPanCardPhoto(null);
        }
        if (profileDTO.getAadharCardPhoto() == null || profileDTO.getAadharCardPhoto().isBlank()) {
            profileDTO.setAadharCardPhoto(null);
        }
        if (profileDTO.getBusinessUtilityBillPhoto() == null || profileDTO.getBusinessUtilityBillPhoto().isBlank()) {
            profileDTO.setBusinessUtilityBillPhoto(null);
        }
        if (profileDTO.getProfilePhoto() == null || profileDTO.getProfilePhoto().isBlank()) {
            profileDTO.setProfilePhoto(null);
        }

        redisTemplate.opsForValue().set("serviceProviderProfile:" + userId, profileDTO);
        return ResponseEntity.ok("Profile data saved and pending admin approval");
    }

}

