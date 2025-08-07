package com.SmartLaundry.service.ServiceProvider;
import com.SmartLaundry.dto.Admin.ServiceProviderRequestDTO;
import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.exception.ForbiddenAccessException;
import com.SmartLaundry.exception.FormatException;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Customer.EmailService;
import com.SmartLaundry.service.Customer.GeoUtils;
import com.SmartLaundry.service.Customer.SMSService;
import com.SmartLaundry.util.UsernameUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceProviderProfileService {
    @Autowired
    private UsernameUtil usernameUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

//    @Autowired
//    private ShedulePlanRepository schedulePlanRepository;

    @Autowired
    private  ItemRepository itemRepository;

    @Autowired
    private GeoUtils geoUtils;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final EmailService emailService;
    private final SMSService smsService;

    private static final Logger logger = LoggerFactory.getLogger(ProviderMyProfileService.class);

    @Value("${FILE_PATH}")
    private String path;

    public ServiceProviderProfileService(EmailService emailService, SMSService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    @Transactional
    public ServiceProviderProfileDTO getServiceProviderProfileDetail(String userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not exist."));

        ServiceProvider serviceProvider = serviceProviderRepository.getByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("Service provider not exist."));

        UserAddress userAddress = userAddressRepository.findByUsers(user);

        // Address DTO mapping
        ServiceProviderProfileDTO.AddressDTO addressDTO = ServiceProviderProfileDTO.AddressDTO.builder()
                .name(userAddress.getName())
                .areaName(userAddress.getAreaName())
                .pincode(userAddress.getPincode())
                .cityName(userAddress.getCity().getCityName())
                .latitude(userAddress.getLatitude())
                .longitude(userAddress.getLongitude())
                .build();

        // Bank Account DTO mapping
        BankAccount bankAccount = serviceProvider.getBankAccount();

        ServiceProviderProfileDTO.BankAccountDTO bankAccountDTO = ServiceProviderProfileDTO.BankAccountDTO.builder()
                .bankName(bankAccount.getBankName())
                .ifscCode(bankAccount.getIfscCode())
                .bankAccountNumber(bankAccount.getBankAccountNumber())
                .accountHolderName(bankAccount.getAccountHolderName())
                .build();


        // PriceDTO list mapping
        List<ServiceProviderProfileDTO.priceDTO> prices = priceRepository.findByServiceProvider(serviceProvider).stream()
                .map(price -> ServiceProviderProfileDTO.priceDTO.builder()
                        .itemId(price.getItem().getItemId())
                        .price(price.getPrice())
                        .serviceProviderId(serviceProvider.getServiceProviderId())
                        .build())
                .toList();

        // Constructing the final DTO
        ServiceProviderProfileDTO profileDTO = ServiceProviderProfileDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNo(user.getPhoneNo())
                .email(user.getEmail())
                .businessName(serviceProvider.getBusinessName())
                .businessLicenseNumber(serviceProvider.getBusinessLicenseNumber())
                .gstNumber(serviceProvider.getGstNumber())
                .needOfDeliveryAgent(serviceProvider.getNeedOfDeliveryAgent())
                .schedulePlans(serviceProvider.getSchedulePlans())
                .photoImage("/image/profile/" + user.getUserId())
                .AadharCardImage("/image/aadhar/" + user.getUserId())
                .PanCardImage("/image/pan/" + user.getUserId())
                .BusinessUtilityBillImage("/image/utility/" + user.getUserId())
                .address(addressDTO)
                .bankAccount(bankAccountDTO)
                .priceDTO(prices)
                .build();

        return profileDTO;
    }
    @Transactional
    public String completeServiceProviderProfile(
            String userId,
            ServiceProviderRequestDTO data,
            MultipartFile aadharCard,
            MultipartFile panCard,
            MultipartFile utilityBill,
            MultipartFile profilePhoto
    ) throws IOException {
        try {
            // 1. User & Role Validation
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));

            if (!UserRole.SERVICE_PROVIDER.equals(user.getRole())) {
                throw new ForbiddenAccessException("Access denied. Not a service provider.");
            }

            // 2. Check duplicate request
            if (serviceProviderRepository.getByUser(user).isPresent()) {
                return "Your request is already submitted. Please wait for a response.";
            }

            // 3. Validate bank account fields
            ServiceProviderRequestDTO.BankAccountDTO bankDTO = data.getBankAccount();
            if (!bankDTO.getAccountHolderName().matches("^[A-Za-z\\s]+$")) {
                throw new FormatException("Account holder name contains invalid characters.");
            }
            if (!bankDTO.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
                throw new FormatException("Invalid IFSC Code format.");
            }

            // 4. Upload images
            String uploadDir = path + "/" + "ServiceProvider" + "/" + "Profile" + "/" + userId + "/";
            String aadharPath = saveFile(aadharCard, uploadDir, userId);
            String panPath = panCard != null ? saveFile(panCard, uploadDir, userId) : null;
            String utilityBillPath = saveFile(utilityBill, uploadDir, userId);
            String profilePath = saveFile(profilePhoto, uploadDir, userId);
            data.setAadharCardPhoto(aadharPath);
            data.setPanCardPhoto(panPath);
            data.setBusinessUtilityBillPhoto(utilityBillPath);
            data.setProfilePhoto(profilePath);

            // 5. Save BankAccount entity
            BankAccount bankAccount = BankAccount.builder()
                    .bankName(bankDTO.getBankName())
                    .ifscCode(bankDTO.getIfscCode())
                    .bankAccountNumber(bankDTO.getBankAccountNumber())
                    .accountHolderName(bankDTO.getAccountHolderName())
                    .build();

            bankAccountRepository.save(bankAccount);
            log.info("Bank account saved with ID: {}", bankAccount.getBankAccountId());

            // 6. Map and Save ServiceProvider
            ServiceProvider serviceProvider = ServiceProvider.builder()
                    .user(user)
                    .businessName(data.getBusinessName())
                    .businessLicenseNumber(data.getBusinessLicenseNumber())
                    .gstNumber(data.getGstNumber())
                    .needOfDeliveryAgent(data.getNeedOfDeliveryAgent())
                    .aadharCardImage(aadharPath)
                    .panCardImage(panPath)
                    .businessUtilityBillImage(utilityBillPath)
                    .photoImage(profilePath)
                    .bankAccount(bankAccount)
                    .schedulePlans(data.getSchedulePlans())
                    .status(Status.PENDING)
                    .build();

            serviceProviderRepository.save(serviceProvider);
            log.info("Service provider saved for user: {}", userId);

            // 7. Save Prices
            if (data.getPriceDTO() != null && !data.getPriceDTO().isEmpty()) {
                List<Price> prices = data.getPriceDTO().stream().map(priceDto -> {
                    if (priceDto.getItem() == null || priceDto.getItem().getItemId() == null) {
                        throw new RuntimeException("Item ID is missing in price data.");
                    }

                    String itemId = priceDto.getItem().getItemId();
                    Items item = itemRepository.findById(itemId)
                            .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

                    return Price.builder()
                            .item(item)
                            .price(priceDto.getPrice())
                            .serviceProvider(serviceProvider)
                            .build();
                }).toList();

                priceRepository.saveAll(prices);
                log.info("Saved {} price entries for service provider {}", prices.size(), serviceProvider.getServiceProviderId());
            }

            // 8. Send Notification
            String message = "Congratulations! Your request to become a Service Provider has been submitted for review.";
            String subject = "Service Provider Profile Submission";

            smsService.sendSms(user.getPhoneNo(), message);
            emailService.sendMail(user.getEmail(), subject, message);

            log.info("Notification sent to user {} via SMS and email", userId);

            return "Your request is sent successfully. Wait for a response.";

        } catch (Exception e) {
            log.error("Error in completing service provider profile for userId {}: {}", userId, e.getMessage(), e);
            throw e; // Required for @Transactional rollback
        }
    }

    public String saveFile(MultipartFile file, String uploadDir, String userId) throws IOException {

        if (file == null || file.isEmpty()) {
            return "File is not exist";
        }

        // Create directory if not exists
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logger.info("Upload dir : {}", dir);

        // Use a unique filename (timestamp + original filename) to avoid collision
        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis()+  "_" + originalFilename;
        logger.info("Original file name : {}", originalFilename);
        logger.info("File name : {}", fileName);

        // Full path
        File destination = new File(dir, fileName);
        logger.info("Destination : {}", destination);

        // Save file locally
        file.transferTo(destination);
        logger.info("File saved successfully");

        // Return the relative or absolute path
        return destination.getAbsolutePath();
    }
}