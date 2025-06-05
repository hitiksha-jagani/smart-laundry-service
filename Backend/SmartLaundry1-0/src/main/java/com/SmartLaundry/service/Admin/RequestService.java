package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.DeliveryAgent.RequestProfileDTO;
import com.SmartLaundry.dto.ServiceProviderProfileDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public RequestService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Logic for accpet service provider request
    public String acceptProvider(String userId){

//        String redisKey = "serviceProviderProfile:" + userId;
//
//        // Fetch from Redis
//        ServiceProviderProfileDTO profileDTO = (ServiceProviderProfileDTO) redisTemplate.opsForValue().get(redisKey);
//        if (profileDTO == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending service providr profile data found.");
//        }
//
//        // Fetch user
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Prevent duplicate approval
//        if (serviceProviderRepository.existsByUser(user)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Service provider already has an approved.");
//        }
//
//        // Save Bank Account
//        BankAccount bankAccount = BankAccount.builder()
//                .bankName(profileDTO.getBankAccount().getBankName())
//                .ifscCode(profileDTO.getBankAccount().getIfscCode())
//                .bankAccountNumber(profileDTO.getBankAccount().getBankAccountNumber())
//                .accountHolderName(profileDTO.getBankAccount().getAccountHolderName())
//                .build();
//        bankAccountRepository.save(bankAccount);
//
//        // Store ServiceProvider data in object
//        ServiceProvider sp = new ServiceProvider();
//        sp.setUser(user);
//        sp.setBusinessName(profileDTO.getBusinessName());
//        sp.setBusinessLicenseNumber(profileDTO.getBusinessLicenseNumber());
//        sp.setGstNumber(profileDTO.getGstNumber());
//        sp.setNeedOfDeliveryAgent(profileDTO.getNeedOfDeliveryAgent());
//        sp.setPhotoImage(profileDTO.getPhotoImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getPhotoImageBase64()) : null);
//        sp.setAadharCardImage(profileDTO.getAadharCardImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getAadharCardImageBase64()) : null);
//        sp.setPanCardImage(profileDTO.getPanCardImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getPanCardImageBase64()) : null);
//        sp.setBusinessUtilityBillImage(profileDTO.getBusinessUtilityBillImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getBusinessUtilityBillImageBase64()) : null);
//        sp.setSchedulePlans(profileDTO.getSchedulePlans());
//        sp.setBankAccount(bankAccount);
//
//        // Save ServiceProvider
//        try {
//            sp = serviceProviderRepository.save(sp);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to save service provider: " + e.getMessage());
//        }
//
//        // Save items and prices
//        List<Items> providerItems = new ArrayList<>();
//        for (ServiceProviderProfileDTO.ItemPriceDTO itemDTO : profileDTO.getItems()) {
//
//            Services service = serviceRepository.findByServiceName(itemDTO.getServiceName())
//                    .orElseGet(() -> {
//                        Services newService = new Services();
//                        newService.setServiceName(itemDTO.getServiceName());
//                        return serviceRepository.save(newService);
//                    });
//
//            SubService subService = subServiceRepository.findBySubServiceNameAndServices(itemDTO.getSubServiceName(), service)
//                    .orElseGet(() -> {
//                        SubService newSubService = new SubService();
//                        newSubService.setSubServiceName(itemDTO.getSubServiceName());
//                        newSubService.setServices(service);
//                        return subServiceRepository.save(newSubService);
//                    });
//
//            Items item = itemRepository.findByItemNameAndServiceAndSubService(itemDTO.getItemName(), service, subService)
//                    .orElseGet(() -> {
//                        Items newItem = new Items();
//                        newItem.setItemName(itemDTO.getItemName());
//                        newItem.setService(service);
//                        newItem.setSubService(subService);
//                        return itemRepository.save(newItem);
//                    });
//
//            providerItems.add(item);
//
//            Price price = new Price();
//            price.setItem(item);
//            price.setServiceProvider(sp);
//            price.setPrice(itemDTO.getPrice());
//            priceRepository.save(price);
//        }
//
//        // Attach items to provider and save
//        sp.setItems(providerItems);
//        serviceProviderRepository.save(sp);
//
//        // Cleanup: Remove from Redis
//        redisTemplate.delete(redisKey);

        return "Successfully accepted.";
    }

    //@author Hitiksha Jagani
    // Logic for reject service provider request
    public String rejectProvider(String userId){

//        String key = "serviceProviderProfile:" + userId;
//        Boolean removed = redisTemplate.delete(key);
//        if (Boolean.TRUE.equals(removed)) {
//            return "Service provider profile rejected.";
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending profile found for rejection.");
//        }
        return "Successfully accepted.";
    }

    //@author Hitiksha Jagani
    // Logic for accpet delivery agent request
    public String acceptAgent(String userId){

//        String redisKey = "DeliveryAgentProfile:" + userId;
//
//        // Fetch from Redis
//        RequestProfileDTO profileDTO = (RequestProfileDTO) redisTemplate.opsForValue().get(redisKey);
//        if (profileDTO == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending delivery agent profile data found");
//        }
//
//        // Fetch user
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 3. Prevent duplicate approval
//        if (serviceProviderRepository.existsByUser(user)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Delivery agent already has an approved.");
//        }
//
//        // Store Delivery agent data in object
//        DeliveryAgent deliveryAgent = new DeliveryAgent();
//        deliveryAgent.setProfilePhoto(profileDTO.getProfilePhoto());
//        deliveryAgent.setGender(profileDTO.getGender());
//        deliveryAgent.setUsers(user);
//        deliveryAgent.setDateOfBirth(profileDTO.getDateOfBirth());
//        deliveryAgent.setVehicleNumber(profileDTO.getVehicleNumber());
//        deliveryAgent.setAadharCardPhoto(profileDTO.getAadharCardPhoto());
//        deliveryAgent.setDrivingLicensePhoto(profileDTO.getDrivingLicensePhoto());
//        deliveryAgent.setPanCardPhoto(profileDTO.getPanCardPhoto());
//        deliveryAgent.setBankName(profileDTO.getBankName());
//        deliveryAgent.setAccountHolderName(profileDTO.getAccountHolderName());
//        deliveryAgent.setBankAccountNumber(profileDTO.getBankAccountNumber());
//        deliveryAgent.setIfscCode(profileDTO.getIfscCode());
//
//        // Save Delivery agent
//        try {
//            deliveryAgentRepository.save(deliveryAgent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to save delivery agent: " + e.getMessage());
//        }
//
//        // Cleanup: Remove from Redis
//        redisTemplate.delete(redisKey);
        return "Successfully accepted.";
    }

    //@author Hitiksha Jagani
    // Logic for reject delivery agent request
    public String rejectAgent(String userId){
        return "Successfully rejected.";
    }
}
