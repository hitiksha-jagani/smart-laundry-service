package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.dto.AddressDTO;
import com.SmartLaundry.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-provider/")
@RequiredArgsConstructor
public class ServiceProviderProfileController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final GeocodingService geocodingService;

    @PostMapping("submit-profile/{userId}")
    public ResponseEntity<String> submitProfile(
            @PathVariable String userId,
            @RequestBody ServiceProviderProfileDTO profileDTO) {

       // AddressDTO address = profileDTO.getBankAccount().getAddress();
//        if (address != null) {
//            String fullAddress = address.getName() + ", " +
//                    address.getAreaName() + ", " +
//                    address.getCity().getName() + ", " +
//                    address.getPincode();
//            GeocodingService.LatLng latLng = geocodingService.getLatLongFromAddress(fullAddress);
//
//            address.setLatitude(latLng.getLatitude());
//            address.setLongitude(latLng.getLongitude());
//        }

        redisTemplate.opsForValue().set("serviceProviderProfile:" + userId, profileDTO);
        return ResponseEntity.ok("Profile data saved and pending admin approval");
    }
}
