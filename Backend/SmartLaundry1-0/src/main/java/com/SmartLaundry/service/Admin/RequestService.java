package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.AcceptAgentDTO;
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


import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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
//        sp.setPhotoImage(profileDTO.getProfilePhoto());
//        sp.setAadharCardImage(profileDTO.getAadharCardPhoto());
//        sp.setPanCardImage(profileDTO.getPanCardPhoto());
//        sp.setBusinessUtilityBillImage(profileDTO.getBusinessUtilityBillPhoto());
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

        List<DeliveryAgent> deliveryAgents = deliveryAgentRepository.findByStatus(Status.PENDING);

        List<RequestProfileDTO> pendingProfiles = new ArrayList<>();

        for (DeliveryAgent agent : deliveryAgents) {

            if (agent == null) continue;

            // Fetch user
            Users user = userRepository.findById(agent.getUsers().getUserId()).orElse(null);
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
                    .dateOfBirth(agent.getDateOfBirth())
                    .vehicleNumber(agent.getVehicleNumber())
                    .bankName(agent.getBankName())
                    .accountHolderName(agent.getAccountHolderName())
                    .bankAccountNumber(agent.getBankAccountNumber())
                    .ifscCode(agent.getIfscCode())
                    .gender(agent.getGender())
                    .addresses(addressDTO)
                    .aadharCardPhoto("/image/aadhar/" + user.getUserId())
                    .profilePhoto("/image/profile/" + user.getUserId())
                    .panCardPhoto("/image/pan/" + user.getUserId())
                    .drivingLicensePhoto("/image/license/" + user.getUserId())
                    .build();

            pendingProfiles.add(requestProfile);
        }

        return pendingProfiles;
    }

    //@author Hitiksha Jagani
    // Logic for accpet delivery agent request
//    @Transactional
    public String acceptAgent(String userId) throws IOException {

        // Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user).orElse(null);
        if(agent == null){
            return "Delivery agent is not found.";
        }

        if(agent.getStatus() != Status.PENDING){
            return "Request for delivery agent profile already accepted/rejected.";
        } else if(agent.getStatus() == Status.ACCEPTED){
            return "Delivery agent is already approved.";
        } else {
            agent.setStatus(Status.ACCEPTED);
        }

        deliveryAgentRepository.save(agent);

        return "Delivery agent profile approved successfully.";
    }

    //@author Hitiksha Jagani
    // Logic for reject delivery agent request
    @Transactional
    public String rejectAgent(String userId){
        String key = "DeliveryAgentProfile:" + userId;

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Delivery agent is not found."));

        Boolean removed = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(removed)) {
            if(deliveryAgent.getStatus() != Status.PENDING){
                return "Request for delivery agent profile already accepted/rejected.";
            } else if(deliveryAgent.getStatus() == Status.REJECTED){
                return "Delivery agent is already rejected.";
            } else {
                deliveryAgent.setStatus(Status.REJECTED);
            }
            return "Delivery Agent profile rejected.";
        } else {
            throw new RuntimeException("No pending delivery agent profile data found.");

        }
    }


}
