package com.SmartLaundry.service.DeliveryAgent;

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
import com.SmartLaundry.service.CloudinaryService;
import com.SmartLaundry.service.Customer.*;
import com.SmartLaundry.util.UsernameUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

//@author Hitiksha Jagani
@Service
public class DeliveryAgentProfileService {

    @Autowired
    private UsernameUtil usernameUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

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

    private static final Logger logger = LoggerFactory.getLogger(DeliveryAgentProfileService.class);

    @Value("${FILE_PATH}")
    private String path;

    private final EmailService emailService;
    private final SMSService smsService;
    // Return profile details of delivery agent

    public DeliveryAgentProfileService(EmailService emailService, SMSService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Transactional
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
        deliveryAgentProfileDTO.setUserId(userId);
        deliveryAgentProfileDTO.setFirstName(user.getFirstName());
        deliveryAgentProfileDTO.setLastName(user.getLastName());
        deliveryAgentProfileDTO.setEmail(user.getEmail());
        deliveryAgentProfileDTO.setPhone(user.getPhoneNo());
        deliveryAgentProfileDTO.setDateOfBirth(deliveryAgent.getDateOfBirth());
        deliveryAgentProfileDTO.setGender(deliveryAgent.getGender());
        deliveryAgentProfileDTO.setVehicleNumber(deliveryAgent.getVehicleNumber());
        deliveryAgentProfileDTO.setProfilePhoto(deliveryAgent.getProfilePhoto());
        //same for profile photo
//        deliveryAgentProfileDTO.setPanCardPhoto("/image/agent/pan/" + user.getUserId());
        deliveryAgentProfileDTO.setPanCardPhoto(deliveryAgent.getPanCardPhoto());
//        deliveryAgentProfileDTO.setAadharCardPhoto("/image/agent/aadhar/" + user.getUserId());
        deliveryAgentProfileDTO.setAadharCardPhoto(deliveryAgent.getAadharCardPhoto());
//        deliveryAgentProfileDTO.setDrivingLicensePhoto("/image/agent/license/" + user.getUserId());
        deliveryAgentProfileDTO.setDrivingLicensePhoto(deliveryAgent.getDrivingLicensePhoto());
        deliveryAgentProfileDTO.setBankName(deliveryAgent.getBankName());
        deliveryAgentProfileDTO.setAccountHolderName(deliveryAgent.getAccountHolderName());
        deliveryAgentProfileDTO.setBankAccountNumber(deliveryAgent.getBankAccountNumber());
        deliveryAgentProfileDTO.setIfscCode(deliveryAgent.getIfscCode());
        deliveryAgentProfileDTO.setAddress(addressDTOs);

        return deliveryAgentProfileDTO;
    }

    // Register delivery agent.
//    @Transactional
    public String completeProfile(String userId, DeliveryAgentCompleteProfileRequestDTO data, MultipartFile aadharCard, MultipartFile panCard, MultipartFile drivingLicense, MultipartFile profilePhoto) throws IOException {

        // Role and user check
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        System.out.println("User found : " + user.getUserId());

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

//        String uploadDir = path + "/" + "DeliveryAgent" + "/" + "Profile" + "/" + userId + "/";
//        // Save files and get paths
//        String aadharCardPath = saveFile(aadharCard, uploadDir, userId);
//        String panCardPath = panCard != null ? saveFile(panCard, uploadDir, userId) : null;
//        String drivingLicensePath = saveFile(drivingLicense, uploadDir, userId);
//        String profilePhotoPath = saveFile(profilePhoto, uploadDir, userId);
        String folder = "DeliveryAgent/Profile/" + userId;

        String aadharCardPath = cloudinaryService.uploadFile(aadharCard, folder);
        String panCardPath = panCard != null ? cloudinaryService.uploadFile(panCard, folder) : null;
        String drivingLicensePath = cloudinaryService.uploadFile(drivingLicense, folder);
        String profilePhotoPath = cloudinaryService.uploadFile(profilePhoto, folder);

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


//        smsService.sendSms(phone, message);
        emailService.sendMail(email, subject, message);

        System.out.println("Your request is sent successfully. Wait for a response.");
        return "Your request is sent successfully. Wait for a response.";
    }

//    public String saveFile(MultipartFile file, String uploadDir, String userId) throws IOException {
//
//        if (file == null || file.isEmpty()) {
//            return "File is not exist";
//        }
//
//        // Create directory if not exists
//        File dir = new File(uploadDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        logger.info("Upload dir : {}", dir);
//
//        // Use a unique filename (timestamp + original filename) to avoid collision
//        String originalFilename = file.getOriginalFilename();
//        String fileName = System.currentTimeMillis()+  "_" + originalFilename;
//        logger.info("Original file name : {}", originalFilename);
//        logger.info("File name : {}", fileName);
//
//        // Full path
//        File destination = new File(dir, fileName);
//        logger.info("Destination : {}", destination);
//
//        // Save file locally
//        file.transferTo(destination);
//        logger.info("File saved successfully");
//
//        // Return the relative or absolute path
//        return destination.getAbsolutePath();
//    }

    // Modify existing details
    @Transactional
    public String editDetail(String userId, DeliveryAgentProfileDTO deliveryAgentProfileDTO, MultipartFile aadharCard,
                             MultipartFile panCard,
                             MultipartFile drivingLicense,
                             MultipartFile profilePhoto) throws IOException {

        // Validation
        if(deliveryAgentProfileDTO.getEmail() != null && !deliveryAgentProfileDTO.getEmail().isBlank()){
            return "Changes in email is not allowed.";
        }

        if(deliveryAgentProfileDTO.getPhone() != null && !deliveryAgentProfileDTO.getPhone().isBlank()){
            return "Changes in phone number is not allowed.";
        }

        if (
                (deliveryAgentProfileDTO.getCurrentLatitude() != null && !deliveryAgentProfileDTO.getCurrentLatitude().isNaN()) ||
                        (deliveryAgentProfileDTO.getAddress().getLatitude() != null && !deliveryAgentProfileDTO.getAddress().getLatitude().isNaN())
        ) {
            return "Changes in latitude is not allowed.";
        }

        if (
                (deliveryAgentProfileDTO.getCurrentLongitude() != null && !deliveryAgentProfileDTO.getCurrentLongitude().isNaN()) ||
                        (deliveryAgentProfileDTO.getAddress().getLongitude() != null && !deliveryAgentProfileDTO.getAddress().getLongitude().isNaN())
        ) {
            return "Changes in longitude is not allowed.";
        }


        // Role and user check
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!UserRole.DELIVERY_AGENT.equals(user.getRole())) {
            throw new ForbiddenAccessException("You are not applicable for this page.");
        }

        // Update basic user info
        if (deliveryAgentProfileDTO.getFirstName() != null) {
            user.setFirstName(deliveryAgentProfileDTO.getFirstName());
        }
        if (deliveryAgentProfileDTO.getLastName() != null) {
            user.setLastName(deliveryAgentProfileDTO.getLastName());
        }

        userRepository.save(user);

        // Update address only if provided
        if (deliveryAgentProfileDTO.getAddress() != null) {
            UserAddress userAddress = userAddressRepository.findByUsers(user);
            DeliveryAgentProfileDTO.AddressDTO address = deliveryAgentProfileDTO.getAddress();

            if (address.getName() != null) {
                userAddress.setName(address.getName());
            }
            if (address.getAreaName() != null) {
                userAddress.setAreaName(address.getAreaName());
            }
            if (address.getPincode() != null) {
                userAddress.setPincode(address.getPincode());
            }
            if (address.getCityName() != null) {
                City city = cityRepository.findByCityName(address.getCityName())
                        .orElseThrow(() -> new RuntimeException("City is not available."));
                userAddress.setCity(city);

                // Build full address and geocode only if city is changed
                String fullAddress = String.format("%s, %s, %s, %s",
                        address.getName() != null ? address.getName() : userAddress.getName(),
                        address.getAreaName() != null ? address.getAreaName() : userAddress.getAreaName(),
                        city.getCityName(),
                        address.getPincode() != null ? address.getPincode() : userAddress.getPincode());

                double[] latLng = geoUtils.getLatLng(fullAddress);
                if (latLng[0] != 0.0 || latLng[1] != 0.0) {
                    userAddress.setLatitude(latLng[0]);
                    userAddress.setLongitude(latLng[1]);
                } else {
                    System.out.println("⚠ Warning: Coordinates could not be determined.");
                }
            }

            userAddressRepository.save(userAddress);
        }

        String aadharCardPath = null;
        String panCardPath = null;
        String drivingLicensePath = null;
        String profilePhotoPath = null;

//        String uploadDir = path + "/" + "DeliveryAgent" + "/" + "Profile" + "/" + userId + "/";
//
//        if (aadharCard != null && !aadharCard.isEmpty()) {
//            aadharCardPath = saveFile(aadharCard, uploadDir, userId);
//        }
//
//        if (panCard != null && !panCard.isEmpty()) {
//            panCardPath = saveFile(panCard, uploadDir, userId);
//        }
//
//        if (drivingLicense != null && !drivingLicense.isEmpty()) {
//            drivingLicensePath = saveFile(drivingLicense, uploadDir, userId);
//        }
//
//        if (profilePhoto != null && !profilePhoto.isEmpty()) {
//            profilePhotoPath = saveFile(profilePhoto, uploadDir, userId);
//        }
        String folder = "DeliveryAgent/Profile/" + userId;

        if (aadharCard != null && !aadharCard.isEmpty()) {
            aadharCardPath = cloudinaryService.uploadFile(aadharCard, folder);
        }
        if (panCard != null && !panCard.isEmpty()) {
            panCardPath = cloudinaryService.uploadFile(panCard, folder);
        }
        if (drivingLicense != null && !drivingLicense.isEmpty()) {
            drivingLicensePath = cloudinaryService.uploadFile(drivingLicense, folder);
        }
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            profilePhotoPath = cloudinaryService.uploadFile(profilePhoto, folder);
        }



        // Update delivery agent only for provided fields
        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Delivery agent not found."));

        if (deliveryAgentProfileDTO.getDateOfBirth() != null) agent.setDateOfBirth(deliveryAgentProfileDTO.getDateOfBirth());
        if (deliveryAgentProfileDTO.getVehicleNumber() != null) agent.setVehicleNumber(deliveryAgentProfileDTO.getVehicleNumber());
        if (deliveryAgentProfileDTO.getBankName() != null) agent.setBankName(deliveryAgentProfileDTO.getBankName());
        if (deliveryAgentProfileDTO.getAccountHolderName() != null) agent.setAccountHolderName(deliveryAgentProfileDTO.getAccountHolderName());
        if (deliveryAgentProfileDTO.getIfscCode() != null) agent.setIfscCode(deliveryAgentProfileDTO.getIfscCode());
        if (deliveryAgentProfileDTO.getGender() != null) agent.setGender(deliveryAgentProfileDTO.getGender());
        if (aadharCard != null) agent.setAadharCardPhoto(aadharCardPath);
        if (panCard != null) agent.setPanCardPhoto(panCardPath);
        if (drivingLicense != null) agent.setDrivingLicensePhoto(drivingLicensePath);
        if (profilePhoto != null) agent.setProfilePhoto(profilePhotoPath);

        deliveryAgentRepository.save(agent);

        return "Profile is edited successfully.";
    }
}