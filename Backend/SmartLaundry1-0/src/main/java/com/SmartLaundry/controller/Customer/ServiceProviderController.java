//package com.SmartLaundry.controller.Customer;
//
//import com.SmartLaundry.dto.CustomerServiceProviderDTO;
//import com.SmartLaundry.model.ServiceProvider;
//import com.SmartLaundry.repository.ServiceProviderRepository;
////import com.SmartLaundry.service.GeoRedisService;
//import com.SmartLaundry.service.NearbyProviderService;
////import com.SmartLaundry.service.ServiceProvider.ServiceProviderService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/customer")
//@RequiredArgsConstructor
//public class ServiceProviderController {
//
////    private final ServiceProviderService serviceProviderService;
////    private final GeoRedisService geoRedisService;
//    private final ServiceProviderRepository serviceProviderRepository;
//    private final NearbyProviderService nearbyProviderService; // ✅ Injected nearby service
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
//    // ✅ New Endpoint for Nearby Filter
//    @GetMapping("/serviceProviders/nearby")
//    public ResponseEntity<List<CustomerServiceProviderDTO>> getNearbyServiceProviders(
//            @RequestParam Double latitude,
//            @RequestParam Double longitude,
//            @RequestParam(defaultValue = "10") double radiusKm) {
//
//        List<ServiceProvider> nearbyProviders = nearbyProviderService.getProvidersNearby(latitude, longitude, radiusKm);
//        List<CustomerServiceProviderDTO> result = nearbyProviders.stream()
//                .map(serviceProviderService::convertToCustomerDTO)
//                .toList();
//
//        return ResponseEntity.ok(result);
//    }
//}
