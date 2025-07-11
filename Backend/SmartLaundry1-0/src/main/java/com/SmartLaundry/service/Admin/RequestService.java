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

        List<ServiceProvider> serviceProviders = serviceProviderRepository.findByStatus(Status.PENDING);

        List<ServiceProviderRequestDTO> pendingProfiles = new ArrayList<>();

        for (ServiceProvider provider : serviceProviders) {

            if (provider == null) continue;

            // Fetch user
            Users user = userRepository.findById(provider.getUser().getUserId()).orElse(null);
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

            ServiceProviderRequestDTO.BankAccountDTO bankAccountDTO = ServiceProviderRequestDTO.BankAccountDTO.builder()
                    .bankName(provider.getBankAccount().getBankName())
                    .ifscCode(provider.getBankAccount().getIfscCode())
                    .bankAccountNumber(provider.getBankAccount().getBankAccountNumber())
                    .accountHolderName(provider.getBankAccount().getAccountHolderName())
                    .build();

            List<Price> priceList = priceRepository.findByServiceProvider(provider);
            List<PriceDTO> priceDTOList = new ArrayList<>();

            if(priceList != null) {

                PriceDTO priceDTO = null;
                for (Price price : priceList) {

                    PriceDTO.ItemDTO itemDTO = PriceDTO.ItemDTO.builder()
                            .itemName(price.getItem().getItemName())
                            .serviceName(
                                    price.getItem() != null && price.getItem().getService() != null
                                            ? price.getItem().getService().getServiceName()
                                            : null
                            )
                            .build();

                    priceDTO = PriceDTO.builder()
                            .item(itemDTO)
                            .price(price.getPrice())
                            .build();

                    priceDTOList.add(priceDTO);
                }
            }

            // Map final DTO
            ServiceProviderRequestDTO requestProfile = ServiceProviderRequestDTO.builder()
                    .userId(user.getUserId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNo(user.getPhoneNo())
                    .email(user.getEmail())
                    .businessName(provider.getBusinessName())
                    .businessLicenseNumber(provider.getBusinessLicenseNumber())
                    .gstNumber(provider.getGstNumber())
                    .bankAccount(bankAccountDTO)
                    .addresses(addressDTO)
                    .aadharCardPhoto("/image/provider/aadhar/" + user.getUserId())
                    .profilePhoto("/image/provider/profile/" + user.getUserId())
                    .panCardPhoto("/image/provider/pan/" + user.getUserId())
                    .businessUtilityBillPhoto("/image/provider/utilitybill/" + user.getUserId())
                    .needOfDeliveryAgent(provider.getNeedOfDeliveryAgent())
                    .priceDTO(priceDTOList)
                    .build();

            pendingProfiles.add(requestProfile);
        }

        return pendingProfiles;

    }

    // Logic for accpet service provider request
    @Transactional
    public String acceptProvider(String userId){

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        String phone = user.getPhoneNo();
        String email = user.getEmail();

        ServiceProvider serviceProvider = serviceProviderRepository.findByUser(user);
        if(serviceProvider == null){
            return "Service provider is not found.";
        }

        if(serviceProvider.getStatus() != Status.PENDING){
            return "Request for Service provider profile already accepted/rejected.";
        } else if(serviceProvider.getStatus() == Status.ACCEPTED){
            return "Service provider is already approved.";
        } else {
            serviceProvider.setStatus(Status.ACCEPTED);
        }

        String message = "Congratulations! Your request to become a Service Provider has been approved.";
        String subject = "Service Provider Approval";

//        smsService.sendOrderStatusNotification(phone, message);
//        emailService.sendOrderStatusNotification(email, subject, message);

        return "Service provider profile approved successfully.";
    }

    //@author Hitiksha Jagani
    // Logic for reject service provider request
    @Transactional
    public String rejectProvider(String userId){

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        String phone = user.getPhoneNo();
        String email = user.getEmail();

        ServiceProvider serviceProvider = serviceProviderRepository.findByUser(user);

        if(serviceProvider.getStatus() != Status.PENDING){
            return "Request for service provider profile already accepted/rejected.";
        } else if(serviceProvider.getStatus() == Status.REJECTED){
            return "Service provider is already rejected.";
        } else {
            serviceProvider.setStatus(Status.REJECTED);
        }

        // Send SMS + Email notification
        String message = "We're sorry! Your request to become a Service Provider has been rejected.";
        String subject = "Service Provider Rejection";

//        smsService.sendOrderStatusNotification(phone, message);
//        emailService.sendOrderStatusNotification(email, subject, message);

        return "Service provider profile rejected.";

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
                    .userId(user.getUserId())
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
                    .aadharCardPhoto("/image/agent/aadhar/" + user.getUserId())
                    .profilePhoto("/image/agent/profile/" + user.getUserId())
                    .panCardPhoto("/image/agent/pan/" + user.getUserId())
                    .drivingLicensePhoto("/image/agent/license/" + user.getUserId())
                    .build();

            pendingProfiles.add(requestProfile);
        }

        return pendingProfiles;
    }

    //@author Hitiksha Jagani
    // Logic for accpet delivery agent request
    @Transactional
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

        // Send SMS + Email notification
        String message = "Congratulations! Your request to become a Delivery agent has been approved.";
        String subject = "Delivery Agent Approval";

//        smsService.sendOrderStatusNotification(phone, message);
//        emailService.sendOrderStatusNotification(email, subject, message);

        return "Delivery agent profile approved successfully.";
    }

    //@author Hitiksha Jagani
    // Logic for reject delivery agent request
    @Transactional
    public String rejectAgent(String userId){

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Delivery agent is not found."));

            if(deliveryAgent.getStatus() != Status.PENDING){
                return "Request for delivery agent profile already accepted/rejected.";
            } else if(deliveryAgent.getStatus() == Status.REJECTED){
                return "Delivery agent is already rejected.";
            } else {
                deliveryAgent.setStatus(Status.REJECTED);
            }

        // Send SMS + Email notification
        String message = "We're sorry! Your request to become a Delivery agent has been rejected.";
        String subject = "Delivery Agent Rejection";

//        smsService.sendOrderStatusNotification(phone, message);
//        emailService.sendOrderStatusNotification(email, subject, message);

            return "Delivery Agent profile rejected.";
    }


}
