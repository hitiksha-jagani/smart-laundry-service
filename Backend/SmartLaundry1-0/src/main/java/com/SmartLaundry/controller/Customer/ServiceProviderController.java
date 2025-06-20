package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.CustomerServiceProviderDTO;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.repository.AddressRepository;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.service.GeoRedisService;
import com.SmartLaundry.service.ServiceProvider.NearbyProviderService;
import com.SmartLaundry.service.Customer.ServiceProviderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//@RestController
//@RequestMapping("/customer")
//@RequiredArgsConstructor
//public class ServiceProviderController {
//
//    private final ServiceProviderService serviceProviderService;
//    private final GeoRedisService geoRedisService;
//    private final ServiceProviderRepository serviceProviderRepository;
//    private final NearbyProviderService nearbyProviderService;
//
//    @GetMapping("/serviceProviders")
//    public ResponseEntity<List<CustomerServiceProviderDTO>> getAllServiceProviders() {
//        List<CustomerServiceProviderDTO> result = serviceProviderService.getAllServiceProvidersForCustomer();
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping("/loadGeoData")
//    public ResponseEntity<String> loadGeoData() {
//        List<ServiceProvider> providers = serviceProviderRepository.findAll();
//        geoRedisService.loadProviderGeoData(providers);
//        return ResponseEntity.ok("Geo data loaded into Redis");
//    }
//
//    // for Nearby Filter
////    @GetMapping("/serviceProviders/nearby")
////    public ResponseEntity<List<CustomerServiceProviderDTO>> getNearbyServiceProviders(
////            @RequestParam Double latitude,
////            @RequestParam Double longitude,
////            @RequestParam(defaultValue = "10") double radiusKm) {
////
////        List<ServiceProvider> nearbyProviders = nearbyProviderService.getProvidersNearby(latitude, longitude, radiusKm);
////        List<CustomerServiceProviderDTO> result = nearbyProviders.stream()
////                .map(serviceProviderService::convertToCustomerDTO)
////                .toList();
////
////        return ResponseEntity.ok(result);
////    }
//
//    @GetMapping("/providers/nearby")
//    public ResponseEntity<List<ServiceProvider>> getNearbyProviders(
//            @RequestParam Double lat,
//            @RequestParam Double lng,
//            @RequestParam(defaultValue = "5") Double radiusKm
//    ) {
//        List<ServiceProvider> providers = nearbyProviderService.getProvidersNearby(lat, lng, radiusKm);
//        return ResponseEntity.ok(providers);
//    }
//}
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;
    private final GeoRedisService geoRedisService;
    private final ServiceProviderRepository serviceProviderRepository;
    private final NearbyProviderService nearbyProviderService;
    private final AddressRepository addressRepository;

    @GetMapping(value = "/serviceProviders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerServiceProviderDTO>> getAllServiceProviders() {
        List<CustomerServiceProviderDTO> result = serviceProviderService.getAllServiceProvidersForCustomer();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/loadGeoData")
    public ResponseEntity<String> loadGeoData() {
        List<ServiceProvider> providers = serviceProviderRepository.findAll();
        geoRedisService.loadProviderGeoData(providers);
        return ResponseEntity.ok("Geo data loaded into Redis");
    }

    @GetMapping("/serviceProviders/nearby")
    public ResponseEntity<List<CustomerServiceProviderDTO>> getNearbyServiceProviders(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5") Double radiusKm
    ) {
        List<ServiceProvider> providers = nearbyProviderService.getProvidersNearby(lat, lng, radiusKm);
        List<CustomerServiceProviderDTO> result = providers.stream()
                .map(serviceProviderService::convertToCustomerDTO)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/location/resolve-pin")
    public ResponseEntity<Map<String, Double>> resolvePin(@RequestParam String pinCode) {
        // Use your existing city or address logic to map PIN to coordinates
        Optional<UserAddress> address = addressRepository.findFirstByPincode(pinCode);
        if (address.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Map<String, Double> coords = Map.of(
                "latitude", address.get().getLatitude(),
                "longitude", address.get().getLongitude()
        );

        return ResponseEntity.ok(coords);
    }
    @GetMapping("/serviceProviders/{id}")
    public ResponseEntity<CustomerServiceProviderDTO> getServiceProviderById(@PathVariable String id) {
        Optional<ServiceProvider> providerOpt = serviceProviderRepository.findById(id);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CustomerServiceProviderDTO dto = serviceProviderService.convertToCustomerDTO(providerOpt.get());
        return ResponseEntity.ok(dto);
    }

}
