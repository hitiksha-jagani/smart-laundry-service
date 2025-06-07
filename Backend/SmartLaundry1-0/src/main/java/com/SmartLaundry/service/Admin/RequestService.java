package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.RequestProfileDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.SMSService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
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

    @Autowired
    private AddressRepository addressRepository;


    private final EmailService emailService;
    private final SMSService smsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(RequestService.class);


    public RequestService(EmailService emailService, SMSService smsService, RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // @author Hitiksha Jagani
    // Return all service provider data which are pending for approval.
    public List<ServiceProviderRequestDTO> getAllProviderProfiles() {
        Set<String> keys = redisTemplate.keys("serviceProviderProfile:*");

        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<ServiceProviderRequestDTO> pendingProfiles = new ArrayList<>();

        for (String key : keys) {
            String userId = key.split(":")[1];

            // Deserialize Redis value into DTO
            Object value = redisTemplate.opsForValue().get(key);
            ServiceProviderRequestDTO profileDTO = objectMapper.convertValue(
                    value, ServiceProviderRequestDTO.class);

            if (profileDTO == null) continue;

            // Fetch user
            Users user = userRepository.findById(userId).orElse(null);
            if (user == null) continue;

            // Fetch address
            UserAddress address = addressRepository.findByUsers(user).orElse(null);
            ServiceProviderRequestDTO.AddressDTO addressDTO = null;
            if (address != null) {
                addressDTO = ServiceProviderRequestDTO.AddressDTO.builder()
                        .name(address.getName())
                        .areaName(address.getAreaName())
                        .pincode(address.getPincode())
                        .cityName(address.getCity().getCityName())
                        .build();
            }

            // Map final DTO
            ServiceProviderRequestDTO requestProfile = ServiceProviderRequestDTO.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNo(user.getPhoneNo())
                    .email(user.getEmail())
                    .aadharCardPhoto(profileDTO.getAadharCardPhoto())
                    .panCardPhoto(profileDTO.getPanCardPhoto())
                    .profilePhoto(profileDTO.getProfilePhoto())
                    .businessName(profileDTO.getBusinessName())
                    .businessLicenseNumber(profileDTO.getBusinessLicenseNumber())
                    .gstNumber(profileDTO.getGstNumber())
                    .businessUtilityBillPhoto(profileDTO.getBusinessUtilityBillPhoto())
                    .schedulePlans(profileDTO.getSchedulePlans())
                    .needOfDeliveryAgent(profileDTO.getNeedOfDeliveryAgent())
                    .addresses(addressDTO)
                    .bankAccount(profileDTO.getBankAccount())
                    .priceDTO(profileDTO.getPriceDTO())
                    .build();

            pendingProfiles.add(requestProfile);
        }

        return pendingProfiles;
    }

    // Logic for accpet service provider request
    @Transactional
    public String acceptProvider(String userId){

        String redisKey = "serviceProviderProfile:" + userId;

        // Fetch from Redis
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            throw new RuntimeException("No pending service provider profile data found.");
        }

        ServiceProviderRequestDTO profileDTO = objectMapper.convertValue(value, ServiceProviderRequestDTO.class);

        // Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent duplicate approval
        if (serviceProviderRepository.existsByUser(user)) {
            throw new RuntimeException("Service provider is already approved.");
        }

        // Save Bank Account
        BankAccount bankAccount = BankAccount.builder()
                .bankName(profileDTO.getBankAccount().getBankName())
                .ifscCode(profileDTO.getBankAccount().getIfscCode())
                .bankAccountNumber(profileDTO.getBankAccount().getBankAccountNumber())
                .accountHolderName(profileDTO.getBankAccount().getAccountHolderName())
                .build();
        bankAccountRepository.save(bankAccount);

        // Store ServiceProvider data in object
        ServiceProvider sp = new ServiceProvider();
        sp.setUser(user);
        sp.setBusinessName(profileDTO.getBusinessName());
        sp.setBusinessLicenseNumber(profileDTO.getBusinessLicenseNumber());
        sp.setGstNumber(profileDTO.getGstNumber());
        sp.setNeedOfDeliveryAgent(profileDTO.getNeedOfDeliveryAgent());
        sp.setPhotoImage(profileDTO.getProfilePhoto());
        sp.setAadharCardImage(profileDTO.getAadharCardPhoto());
        sp.setPanCardImage(profileDTO.getPanCardPhoto());
        sp.setBusinessUtilityBillImage(profileDTO.getBusinessUtilityBillPhoto());
        sp.setSchedulePlans(profileDTO.getSchedulePlans());
        sp.setBankAccount(bankAccount);
        sp.setPrices(null);
        ServiceProvider savedSP = serviceProviderRepository.save(sp);

        // Save items and prices
        List<PriceDTO> prices = profileDTO.getPriceDTO();
        List<Price> providerPrices = new ArrayList<>();

        for (PriceDTO priceDTO : prices) {

            // Fetch the item using itemId
            Items item = itemRepository.findById(priceDTO.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Item with ID " + priceDTO.getItem().getItemId() + " is not available."));

            // Create a Price entity
            Price price = new Price();
            price.setItem(item);
            price.setServiceProvider(sp);
            price.setPrice(priceDTO.getPrice());

            // Save the price
            priceRepository.save(price);
            providerPrices.add(price);
        }

        // Set the list of prices in ServiceProvider
        savedSP.setPrices(providerPrices);

        // Save ServiceProvider
        try {
            serviceProviderRepository.save(savedSP);
        } catch (Exception e) {
            throw new RuntimeException("Save failed", e);
        }
        String phone = user.getPhoneNo();
        String email = user.getEmail();

        String message = "Congratulations! Your request to become a Service Provider has been approved.";
        String subject = "Service Provider Approval";

        smsService.sendOrderStatusNotification(phone, message); // Or general-purpose SMS method
        emailService.sendOrderStatusNotification(email, subject, message);
        // Cleanup: Remove from Redis
        redisTemplate.delete(redisKey);


        return "Service provider profile approved successfully.";
    }

    //@author Hitiksha Jagani
    // Logic for reject service provider request
    @Transactional
    public String rejectProvider(String userId){

        String key = "serviceProviderProfile:" + userId;

        Boolean removed = redisTemplate.delete(key);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String phone = user.getPhoneNo();
        String email = user.getEmail();

        if (Boolean.TRUE.equals(removed)) {
            // Send SMS + Email notification
            String message = "We're sorry! Your request to become a Service Provider has been rejected.";
            String subject = "Service Provider Rejection";

            smsService.sendOrderStatusNotification(phone, message);
            emailService.sendOrderStatusNotification(email, subject, message);

            return "Service provider profile rejected.";
        } else {
            throw new RuntimeException("No pending service provider profile data found.");
        }

    }

    // @author Hitiksha Jagani
    // Return all delivery agent data which are pending for approval.
    public List<RequestProfileDTO> getAllAgentProfiles() {
        Set<String> keys = redisTemplate.keys("DeliveryAgentProfile:*");

        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<RequestProfileDTO> pendingProfiles = new ArrayList<>();

        for (String key : keys) {
            String userId = key.split(":")[1];

            // Deserialize Redis value into DTO
            Object value = redisTemplate.opsForValue().get(key);
            DeliveryAgentCompleteProfileRequestDTO profileDTO = objectMapper.convertValue(
                    value, DeliveryAgentCompleteProfileRequestDTO.class);

            if (profileDTO == null) continue;

            // Fetch user
            Users user = userRepository.findById(userId).orElse(null);
            if (user == null) continue;

            // Fetch address
            UserAddress address = addressRepository.findByUsers(user).orElse(null);
            RequestProfileDTO.AddressDTO addressDTO = null;
            if (address != null) {
                addressDTO = RequestProfileDTO.AddressDTO.builder()
                        .name(address.getName())
                        .areaName(address.getAreaName())
                        .pincode(address.getPincode())
                        .cityName(address.getCity().getCityName())
                        .build();
            }

            // Map final DTO
            RequestProfileDTO requestProfile = RequestProfileDTO.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNo(user.getPhoneNo())
                    .email(user.getEmail())
                    .dateOfBirth(profileDTO.getDateOfBirth())
                    .vehicleNumber(profileDTO.getVehicleNumber())
                    .aadharCardPhoto(profileDTO.getAadharCardPhoto())
                    .panCardPhoto(profileDTO.getPanCardPhoto())
                    .drivingLicensePhoto(profileDTO.getDrivingLicensePhoto())
                    .bankName(profileDTO.getBankName())
                    .accountHolderName(profileDTO.getAccountHolderName())
                    .bankAccountNumber(profileDTO.getBankAccountNumber())
                    .ifscCode(profileDTO.getIfscCode())
                    .profilePhoto(profileDTO.getProfilePhoto())
                    .gender(profileDTO.getGender())
                    .addresses(addressDTO)
                    .build();

            pendingProfiles.add(requestProfile);
        }

        return pendingProfiles;
    }

    //@author Hitiksha Jagani
    // Logic for accpet delivery agent request
    @Transactional
    public String acceptAgent(String userId) {
        String redisKey = "DeliveryAgentProfile:" + userId;

        // Fetch and convert from Redis
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            throw new RuntimeException("No pending delivery agent profile data found.");
        }

        RequestProfileDTO profileDTO = objectMapper.convertValue(value, RequestProfileDTO.class);

        // Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Prevent duplicate approval
        if (deliveryAgentRepository.existsByUsers(user)) {
            throw new RuntimeException("Delivery agent is already approved.");
        }

        // Map data to DeliveryAgent entity
        DeliveryAgent deliveryAgent = new DeliveryAgent();
        deliveryAgent.setProfilePhoto(profileDTO.getProfilePhoto());
        deliveryAgent.setGender(profileDTO.getGender());
        deliveryAgent.setUsers(user);
        deliveryAgent.setDateOfBirth(profileDTO.getDateOfBirth());
        deliveryAgent.setVehicleNumber(profileDTO.getVehicleNumber());
        deliveryAgent.setAadharCardPhoto(profileDTO.getAadharCardPhoto());
        deliveryAgent.setDrivingLicensePhoto(profileDTO.getDrivingLicensePhoto());
        deliveryAgent.setPanCardPhoto(profileDTO.getPanCardPhoto());
        deliveryAgent.setBankName(profileDTO.getBankName());
        deliveryAgent.setAccountHolderName(profileDTO.getAccountHolderName());
        deliveryAgent.setBankAccountNumber(profileDTO.getBankAccountNumber());
        deliveryAgent.setIfscCode(profileDTO.getIfscCode());

        try {
            deliveryAgentRepository.save(deliveryAgent);
        } catch (Exception e) {
            throw new RuntimeException("Save failed", e);
        }

        // Remove from Redis
        redisTemplate.delete(redisKey);

        return "Delivery agent profile approved successfully.";
    }


    //@author Hitiksha Jagani
    // Logic for reject delivery agent request
    @Transactional
    public String rejectAgent(String userId){
        String key = "DeliveryAgentProfile:" + userId;

        Boolean removed = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(removed)) {
            return "Delivery Agent profile rejected.";
        } else {
            throw new RuntimeException("No pending delivery agent profile data found.");

        }
    }

}
