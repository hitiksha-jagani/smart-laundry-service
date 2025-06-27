package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.exception.ForbiddenAccessException;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    @Value("${SERVICE_PROVIDER_PROFILE_IMAGE}")
    private String path;

//    @Autowired
//    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private GeoUtils geoUtils;

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

        // your role check ...

        // update user and address code ...

        ServiceProvider sp = serviceProviderRepository.getByUser(user)
                .orElseThrow(() -> new RuntimeException("Service Provider profile not found."));

        // Save files if provided, update paths in DTO & SP
        String uploadDir = path + userId + "/";

        if (aadharCard != null && !aadharCard.isEmpty()) {
            String aadharPath = saveFile(aadharCard, uploadDir, userId);
            profileDTO.setAadharCardImage(aadharPath);
            sp.setAadharCardImage(aadharPath);
        }

        if (panCard != null && !panCard.isEmpty()) {
            String panPath = saveFile(panCard, uploadDir, userId);
            profileDTO.setPanCardImage(panPath);
            sp.setPanCardImage(panPath);
        }

        if (utilityBill != null && !utilityBill.isEmpty()) {
            String utilityBillPath = saveFile(utilityBill, uploadDir, userId);
            profileDTO.setBusinessUtilityBillImage(utilityBillPath);
            sp.setBusinessUtilityBillImage(utilityBillPath);
        }

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String profilePath = saveFile(profilePhoto, uploadDir, userId);
            profileDTO.setPhotoImage(profilePath);
            sp.setPhotoImage(profilePath);
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

