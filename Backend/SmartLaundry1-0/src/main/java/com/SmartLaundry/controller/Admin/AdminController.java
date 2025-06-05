package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/admin/")
@RequiredArgsConstructor
public class AdminController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final SubServiceRepository subServiceRepository;
    private final ItemsRepository itemsRepository;
    private final PriceRepository priceRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    @PostMapping("/approve-service-provider/{userId}")
    public ResponseEntity<String> approveServiceProvider(@PathVariable String userId) {

        // 1. Fetch profile DTO from Redis
        ServiceProviderProfileDTO profileDTO = (ServiceProviderProfileDTO) redisTemplate.opsForValue()
                .get("serviceProviderProfile:" + userId);

        if (profileDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No pending profile data found");
        }

        // 2. Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Check if already approved
        if (serviceProviderRepository.existsByUser(user)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already has an approved service provider profile");
        }

        // 4. Save bank account
        BankAccount bankAccount = BankAccount.builder()
                .bankName(profileDTO.getBankAccount().getBankName())
                .ifscCode(profileDTO.getBankAccount().getIfscCode())
                .bankAccountNumber(profileDTO.getBankAccount().getBankAccountNumber())
                .accountHolderName(profileDTO.getBankAccount().getAccountHolderName())
                .build();
        bankAccount = bankAccountRepository.save(bankAccount);

        // 5. Build ServiceProvider entity
        ServiceProvider sp = new ServiceProvider();
        sp.setUser(user);
        sp.setBusinessName(profileDTO.getBusinessName());
        sp.setBusinessLicenseNumber(profileDTO.getBusinessLicenseNumber());
        sp.setGstNumber(profileDTO.getGstNumber());
        sp.setNeedOfDeliveryAgent(profileDTO.getNeedOfDeliveryAgent());
        sp.setPhotoImage(profileDTO.getPhotoImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getPhotoImageBase64()) : null);
        sp.setAadharCardImage(profileDTO.getAadharCardImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getAadharCardImageBase64()) : null);
        sp.setPanCardImage(profileDTO.getPanCardImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getPanCardImageBase64()) : null);
        sp.setBusinessUtilityBillImage(profileDTO.getBusinessUtilityBillImageBase64() != null ? Base64.getDecoder().decode(profileDTO.getBusinessUtilityBillImageBase64()) : null);
        sp.setSchedulePlans(profileDTO.getSchedulePlans());
        sp.setBankAccount(bankAccount);

        // 6. Save ServiceProvider and handle possible errors
        try {
            sp = serviceProviderRepository.save(sp);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Constraint violation: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save service provider: " + e.getMessage());
        }

        // 7. Save items and prices
        List<Items> providerItems = new ArrayList<>();
        for (ServiceProviderProfileDTO.ItemPriceDTO itemDTO : profileDTO.getItems()) {

            Services service = serviceRepository.findByServiceName(itemDTO.getServiceName())
                    .orElseGet(() -> {
                        Services newService = new Services();
                        newService.setServiceName(itemDTO.getServiceName());
                        return serviceRepository.save(newService);
                    });

            SubService subService = subServiceRepository.findBySubServiceNameAndServices(itemDTO.getSubServiceName(), service)
                    .orElseGet(() -> {
                        SubService newSubService = new SubService();
                        newSubService.setSubServiceName(itemDTO.getSubServiceName());
                        newSubService.setServices(service);
                        return subServiceRepository.save(newSubService);
                    });

            Items item = itemsRepository.findByItemNameAndServiceAndSubService(itemDTO.getItemName(), service, subService)
                    .orElseGet(() -> {
                        Items newItem = new Items();
                        newItem.setItemName(itemDTO.getItemName());
                        newItem.setService(service);
                        newItem.setSubService(subService);
                        return itemsRepository.save(newItem);
                    });

            providerItems.add(item);

            Price price = new Price();
            price.setItem(item);
            price.setServiceProvider(sp);
            price.setPrice(itemDTO.getPrice());
            priceRepository.save(price);
        }

        // 8. Update ServiceProvider with items list
        sp.setItems(providerItems);
        serviceProviderRepository.save(sp);

        // 9. Remove profile from Redis
        redisTemplate.delete("serviceProviderProfile:" + userId);

        return ResponseEntity.ok("Service provider profile approved and saved successfully");
    }
}
