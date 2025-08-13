package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.exception.ForbiddenAccessException;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.CloudinaryService;
import com.SmartLaundry.service.Customer.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.SmartLaundry.service.Customer.GeoUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderMyProfileService {
    @Autowired
    private final BlockOffDayRepository blockOffDayRepository;
    @Autowired
    private final ServiceProviderRepository serviceProviderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Value("${FILE_PATH}")
    private String path;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private GeoUtils geoUtils;
    @Autowired
    private CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(ProviderMyProfileService.class);

    @Autowired
    private BankAccountRepository bankAccountRepository;
    public void markBlockOffDays(String providerId, List<LocalDate> blockDays) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        // Clear existing
        blockOffDayRepository.deleteAll(blockOffDayRepository.findByServiceProvider(provider));

        // Save new
        List<BlockOffDay> days = blockDays.stream()
                .map(date -> new BlockOffDay(null, provider, date))
                .toList();

        blockOffDayRepository.saveAll(days);
    }

    public List<LocalDate> getBlockOffDays(String providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        return blockOffDayRepository.findByServiceProvider(provider)
                .stream().map(BlockOffDay::getDate).toList();
    }

    @Transactional
    public String editServiceProviderDetail(
            String userId,
            ServiceProviderProfileDTO profileDTO,
            MultipartFile aadharCard,
            MultipartFile panCard,
            MultipartFile utilityBill,
            MultipartFile profilePhoto
    ) throws IOException {
        // existing checks ...

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        ServiceProvider sp = serviceProviderRepository.getByUser(user)
                .orElseThrow(() -> new RuntimeException("Service Provider profile not found."));

        // Save files if provided, update paths in DTO & SP
        String uploadDir = path + "/" + "ServiceProvider" + "/" + "Profile" + "/" + userId + "/";
        logger.info("Upload files : {}", uploadDir);

        if (aadharCard != null && !aadharCard.isEmpty()) {
            String cloudinaryUrl = cloudinaryService.uploadFile(aadharCard, "service_providers/aadhar");
            profileDTO.setAadharCardImage(cloudinaryUrl);
            sp.setAadharCardImage(cloudinaryUrl);
        }

        if (panCard != null && !panCard.isEmpty()) {
            String cloudinaryUrl = cloudinaryService.uploadFile(panCard, "service_providers/pan_cards");
            profileDTO.setPanCardImage(cloudinaryUrl);
            sp.setPanCardImage(cloudinaryUrl);
        }

        if (utilityBill != null && !utilityBill.isEmpty()) {
            String cloudinaryUrl = cloudinaryService.uploadFile(utilityBill, "service_providers/utility_bills");
            profileDTO.setBusinessUtilityBillImage(cloudinaryUrl);
            sp.setBusinessUtilityBillImage(cloudinaryUrl);
        }

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String cloudinaryUrl = cloudinaryService.uploadFile(profilePhoto, "service_providers");
            profileDTO.setPhotoImage(cloudinaryUrl);
            sp.setPhotoImage(cloudinaryUrl);
        }


        // update other fields from profileDTO
        sp.setBusinessName(profileDTO.getBusinessName());
        sp.setBusinessLicenseNumber(profileDTO.getBusinessLicenseNumber());
        sp.setGstNumber(profileDTO.getGstNumber());
        sp.setNeedOfDeliveryAgent(profileDTO.getNeedOfDeliveryAgent());
        sp.setSchedulePlans(profileDTO.getSchedulePlans());

        // update bank info (existing code)
        BankAccount bank = sp.getBankAccount();
        bank.setBankName(profileDTO.getBankAccount().getBankName());
        bank.setIfscCode(profileDTO.getBankAccount().getIfscCode());
        bank.setBankAccountNumber(profileDTO.getBankAccount().getBankAccountNumber());
        bank.setAccountHolderName(profileDTO.getBankAccount().getAccountHolderName());
        bankAccountRepository.save(bank);
        sp.setBankAccount(bank);

        serviceProviderRepository.save(sp);

        // update prices (existing code)

        return "Service provider profile has been updated successfully.";
    }

}

