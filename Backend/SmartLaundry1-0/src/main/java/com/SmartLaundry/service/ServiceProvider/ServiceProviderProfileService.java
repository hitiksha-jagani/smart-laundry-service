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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
    // Return profile details of delivery agent

    public ServiceProviderProfileService(EmailService emailService, SMSService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

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
        String uploadDir = "D:\\MSCIT\\summerinternship\\images\\service_providers" + userId;
        String aadharPath = saveFile(aadharCard, uploadDir, userId);
        String panPath = panCard != null ? saveFile(panCard, uploadDir, userId) : null;
        String utilityBillPath = saveFile(utilityBill, uploadDir, userId);
        String profilePath = saveFile(profilePhoto, uploadDir, userId);

        // 5. Save Address with Geo location
        ServiceProviderRequestDTO.AddressDTO addressDTO = data.getAddresses();
        City city = cityRepository.findByCityName(addressDTO.getCityName())
                .orElseThrow(() -> new RuntimeException("City not found."));

        UserAddress address = UserAddress.builder()
                .users(user)
                .name(addressDTO.getName())
                .areaName(addressDTO.getAreaName())
                .pincode(addressDTO.getPincode())
                .city(city)
                .build();

        String fullAddress = String.format("%s, %s, %s, %s",
                addressDTO.getName(), addressDTO.getAreaName(), city.getCityName(), addressDTO.getPincode());

        double[] latLng = geoUtils.getLatLng(fullAddress);
        if (latLng[0] != 0.0 || latLng[1] != 0.0) {
            address.setLatitude(latLng[0]);
            address.setLongitude(latLng[1]);
        }

        userAddressRepository.save(address);

        // 6. Save BankAccount entity
        BankAccount bankAccount = BankAccount.builder()
                .bankName(bankDTO.getBankName())
                .ifscCode(bankDTO.getIfscCode())
                .bankAccountNumber(bankDTO.getBankAccountNumber())
                .accountHolderName(bankDTO.getAccountHolderName())
                .build();

        bankAccountRepository.save(bankAccount);

        // 7. Map and Save ServiceProvider
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
                .build();

        serviceProviderRepository.save(serviceProvider);

        // 8. Save Prices

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
        }




        // 9. Send Notification
        String message = "Congratulations! Your request to become a Service Provider has been submitted for review.";
        String subject = "Service Provider Profile Submission";

        smsService.sendSms(user.getPhoneNo(), message);
        emailService.sendMail(user.getEmail(), subject, message);

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





}
