package com.SmartLaundry.service.DeliveryAgent;

import com.SmartLaundry.dto.AddressDTO;
import com.SmartLaundry.dto.Admin.DeliveryAgentResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentCompleteProfileRequestDTO;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentProfileDTO;
import com.SmartLaundry.dto.DeliveryAgent.RequestProfileDTO;
import com.SmartLaundry.exception.ExceptionMsg;
import com.SmartLaundry.exception.ForbiddenAccessException;
import com.SmartLaundry.exception.FormatException;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.CityRepository;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.UserAddressRepository;
import com.SmartLaundry.repository.UserRepository;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.GeoUtils;
import com.SmartLaundry.service.Customer.SMSService;
import com.SmartLaundry.util.UsernameUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.SMSService;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryAgentProfileService {

    @Autowired
    private UsernameUtil usernameUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private GeoUtils geoUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    private final EmailService emailService;
    private final SMSService smsService;
    // Return profile details of delivery agent

    public DeliveryAgentProfileService(EmailService emailService, SMSService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    public DeliveryAgentProfileDTO getProfileDetail(String userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not exist."));

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));

        UserAddress userAddresses = userAddressRepository.findByUsers(user);

        DeliveryAgentProfileDTO.AddressDTO addressDTOs = DeliveryAgentProfileDTO.AddressDTO.builder()
                .name(userAddresses.getName())
                .areaName(userAddresses.getAreaName())
                .pincode(userAddresses.getPincode())
                .cityName(userAddresses.getCity().getCityName())
                .latitude(userAddresses.getLatitude())
                .longitude(userAddresses.getLongitude())
                .build();

        DeliveryAgentProfileDTO deliveryAgentProfileDTO = new DeliveryAgentProfileDTO();
        deliveryAgentProfileDTO.setFirstName(user.getFirstName());
        deliveryAgentProfileDTO.setLastName(user.getLastName());
        deliveryAgentProfileDTO.setEmail(user.getEmail());
        deliveryAgentProfileDTO.setPhone(user.getPhoneNo());
        deliveryAgentProfileDTO.setDateOfBirth(deliveryAgent.getDateOfBirth());
        deliveryAgentProfileDTO.setGender(deliveryAgent.getGender());
        deliveryAgentProfileDTO.setVehicleNumber(deliveryAgent.getVehicleNumber());
        deliveryAgentProfileDTO.setProfilePhoto("/image/profile/" + user.getUserId());
        deliveryAgentProfileDTO.setPanCardPhoto("/image/pan/" + user.getUserId());
        deliveryAgentProfileDTO.setAadharCardPhoto("/image/aadhar/" + user.getUserId());
        deliveryAgentProfileDTO.setDrivingLicensePhoto("/image/license/" + user.getUserId());
        deliveryAgentProfileDTO.setBankName(deliveryAgent.getBankName());
        deliveryAgentProfileDTO.setAccountHolderName(deliveryAgent.getAccountHolderName());
        deliveryAgentProfileDTO.setBankAccountNumber(deliveryAgent.getBankAccountNumber());
        deliveryAgentProfileDTO.setIfscCode(deliveryAgent.getIfscCode());
        deliveryAgentProfileDTO.setAddress(addressDTOs);

        return deliveryAgentProfileDTO;
    }

    // Register delivery agent.
    @Transactional
    public String completeProfile(String userId, DeliveryAgentCompleteProfileRequestDTO data, MultipartFile aadharCard, MultipartFile panCard, MultipartFile drivingLicense, MultipartFile profilePhoto) throws IOException {

        // Role and user check
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!UserRole.DELIVERY_AGENT.equals(user.getRole())) {
            throw new ForbiddenAccessException("You are not applicable for this page.");
        }

        // Check whether delivery agent already has pending approval
        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user).orElse(null);
        if (agent != null) {
            return "Your request is already send wait for response.";
        }

        // Validate name
        if (!data.getAccountHolderName().matches("^[A-Za-z\\s]+$")) {
            throw new FormatException("Account holder name contains invalid characters.");
        }

        if (!data.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
            throw new FormatException("Invalid IFSC Code format.");
        }

        String uploadDir = "/media/hitiksha/C/DAIICT/Summer internship/images/delivery_agents/" + userId;

        // Save files and get paths
        String aadharCardPath = saveFile(aadharCard, uploadDir, userId);
        String panCardPath = panCard != null ? saveFile(panCard, uploadDir, userId) : null;
        String drivingLicensePath = saveFile(drivingLicense, uploadDir, userId);
        String profilePhotoPath = saveFile(profilePhoto, uploadDir, userId);

        // Map final DTO
        DeliveryAgent requestProfile = DeliveryAgent.builder()
                .users(user)
                .dateOfBirth(data.getDateOfBirth())
                .vehicleNumber(data.getVehicleNumber())
                .bankName(data.getBankName())
                .currentLatitude((user.getAddress() != null) ? user.getAddress().getLatitude() : null)
                .currentLongitude((user.getAddress() != null) ? user.getAddress().getLongitude() : null)
                .accountHolderName(data.getAccountHolderName())
                .bankAccountNumber(data.getBankAccountNumber())
                .ifscCode(data.getIfscCode())
                .gender(data.getGender())
                .aadharCardPhoto(aadharCardPath)
                .panCardPhoto(panCardPath)
                .profilePhoto(profilePhotoPath)
                .drivingLicensePhoto(drivingLicensePath)
                .status(Status.PENDING)
                .build();

        deliveryAgentRepository.save(requestProfile);

        String phone = user.getPhoneNo();
        String email = user.getEmail();

        String message = "Congratulations! Your request to become a Delivery Agent has been submitted for review.";
        String subject = "Delivery Agent Profile Submission";


        smsService.sendSms(phone, message);
        emailService.sendMail(email, subject, message);

        return "Your request is sent successfully. Wait for a response.";
    }

    public String saveFile(MultipartFile file, String uploadDir, String userId) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        // Create directory if not exists
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Use a unique filename (timestamp + original filename) to avoid collision
        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis()+  "_" + originalFilename;

        // Full path
        File destination = new File(dir, fileName);

        // Save file locally
        file.transferTo(destination);

        // Return the relative or absolute path
        return destination.getAbsolutePath();
    }

    // Modify existing details
    @Transactional
    public String editDetail(String userId, DeliveryAgentProfileDTO deliveryAgentProfileDTO) {

        // Validation
        if(deliveryAgentProfileDTO.getEmail() != null || !deliveryAgentProfileDTO.getEmail().isBlank()){
            return "Changes in email is not allowed.";
        }

        if(deliveryAgentProfileDTO.getPhone() != null || !deliveryAgentProfileDTO.getPhone().isBlank()){
            return "Changes in phone number is not allowed.";
        }

        if((deliveryAgentProfileDTO.getCurrentLatitude() != null || !deliveryAgentProfileDTO.getCurrentLatitude().isNaN()) || (deliveryAgentProfileDTO.getAddress().getLatitude() != null || !deliveryAgentProfileDTO.getAddress().getLatitude().isNaN())){
            return "Changes in latitude is not allowed.";
        }

        if((deliveryAgentProfileDTO.getCurrentLongitude() != null || !deliveryAgentProfileDTO.getCurrentLongitude().isNaN()) || (deliveryAgentProfileDTO.getAddress().getLongitude() != null || !deliveryAgentProfileDTO.getAddress().getLongitude().isNaN())){
            return "Changes in longitude is not allowed.";
        }

        // Role and user check
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!UserRole.DELIVERY_AGENT.equals(user.getRole())) {
            throw new ForbiddenAccessException("You are not applicable for this page.");
        }

        user = Users.builder()
                .firstName(deliveryAgentProfileDTO.getFirstName())
                .lastName(deliveryAgentProfileDTO.getLastName())
                .build();

        userRepository.save(user);

        UserAddress userAddress = userAddressRepository.findByUsers(user);
        City city = cityRepository.findByCityName(deliveryAgentProfileDTO.getAddress().getCityName())
                .orElseThrow(() -> new RuntimeException("City is not available."));

        userAddress = UserAddress.builder()
                .name(deliveryAgentProfileDTO.getAddress().getName())
                .areaName(deliveryAgentProfileDTO.getAddress().getAreaName())
                .pincode(deliveryAgentProfileDTO.getAddress().getPincode())
                .city(city)
                .build();

        // Build full address string for geocoding
        String fullAddress = String.format("%s, %s, %s, %s",
                deliveryAgentProfileDTO.getAddress().getName(),
                deliveryAgentProfileDTO.getAddress().getAreaName(),
                city.getCityName(),
                deliveryAgentProfileDTO.getAddress().getPincode());

        // Call utility to get coordinates
        double[] latLng = geoUtils.getLatLng(fullAddress);

        // Set latitude & longitude if found
        if (latLng[0] != 0.0 || latLng[1] != 0.0) {
            userAddress.setLatitude(latLng[0]);
            userAddress.setLongitude(latLng[1]);
        } else {
            System.out.println("⚠ Warning: Coordinates could not be determined.");
        }

        userAddressRepository.save(userAddress);

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElseThrow();
        deliveryAgent = DeliveryAgent.builder()
                .users(user)
                .dateOfBirth(deliveryAgentProfileDTO.getDateOfBirth())
                .vehicleNumber(deliveryAgentProfileDTO.getVehicleNumber())
                .bankName(deliveryAgentProfileDTO.getBankName())
                .accountHolderName(deliveryAgentProfileDTO.getAccountHolderName())
                .ifscCode(deliveryAgentProfileDTO.getIfscCode())
                .gender(deliveryAgentProfileDTO.getGender())
                .aadharCardPhoto(deliveryAgentProfileDTO.getAadharCardPhoto())
                .panCardPhoto(deliveryAgentProfileDTO.getPanCardPhoto())
                .drivingLicensePhoto(deliveryAgentProfileDTO.getDrivingLicensePhoto())
                .profilePhoto(deliveryAgentProfileDTO.getProfilePhoto())
                .build();

        deliveryAgentRepository.save(deliveryAgent);

        return "Profile is edited successfully.";
    }
}