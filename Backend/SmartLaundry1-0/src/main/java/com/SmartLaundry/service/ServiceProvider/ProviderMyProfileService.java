package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.ServiceProvider.ServiceProviderProfileDTO;
import com.SmartLaundry.exception.ForbiddenAccessException;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.SmartLaundry.service.Customer.GeoUtils;
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
    public String editServiceProviderDetail(String userId, ServiceProviderProfileDTO profileDTO) {
        System.out.println("Incoming address: " + profileDTO.getAddress());

        // Validation for restricted fields
        if (profileDTO.getEmail() != null && !profileDTO.getEmail().isBlank()) {
            return "Changes in email are not allowed.";
        }

        if (profileDTO.getPhoneNo() != null && !profileDTO.getPhoneNo().isBlank()) {
            return "Changes in phone number are not allowed.";
        }

        if (profileDTO.getAddress() != null &&
                ((profileDTO.getAddress().getLatitude() != null && !profileDTO.getAddress().getLatitude().isNaN()) ||
                        (profileDTO.getAddress().getLongitude() != null && !profileDTO.getAddress().getLongitude().isNaN()))) {
            return "Changes in coordinates are not allowed.";
        }


        // Fetch user and verify role
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!UserRole.SERVICE_PROVIDER.equals(user.getRole())) {
            throw new ForbiddenAccessException("You are not authorized to edit service provider profile.");
        }

        // Update user name
        user.setFirstName(profileDTO.getFirstName());
        user.setLastName(profileDTO.getLastName());
        userRepository.save(user);

        // Update address
        if (profileDTO.getAddress() == null) {
            throw new IllegalArgumentException("Address information is required.");
        }
        UserAddress address = userAddressRepository.findByUsers(user);
        City city = cityRepository.findByCityName(profileDTO.getAddress().getCityName())
                .orElseThrow(() -> new RuntimeException("City is not available."));


        address.setName(profileDTO.getAddress().getName());
        address.setAreaName(profileDTO.getAddress().getAreaName());
        address.setPincode(profileDTO.getAddress().getPincode());
        address.setCity(city);

        // Geocode full address
        String fullAddress = String.format("%s, %s, %s, %s",
                profileDTO.getAddress().getName(),
                profileDTO.getAddress().getAreaName(),
                city.getCityName(),
                profileDTO.getAddress().getPincode());

        double[] latLng = geoUtils.getLatLng(fullAddress);
        if (latLng[0] != 0.0 || latLng[1] != 0.0) {
            address.setLatitude(latLng[0]);
            address.setLongitude(latLng[1]);
        }

        userAddressRepository.save(address);

        // Update ServiceProvider details
        ServiceProvider sp = serviceProviderRepository.getByUser(user)
                .orElseThrow(() -> new RuntimeException("Service Provider profile not found."));

        sp.setBusinessName(profileDTO.getBusinessName());
        sp.setBusinessLicenseNumber(profileDTO.getBusinessLicenseNumber());
        sp.setGstNumber(profileDTO.getGstNumber());
        sp.setNeedOfDeliveryAgent(profileDTO.getNeedOfDeliveryAgent());
        sp.setPhotoImage(profileDTO.getProfilePhoto());
        sp.setAadharCardImage(profileDTO.getAadharCardPhoto());
        sp.setPanCardImage(profileDTO.getPanCardPhoto());
        sp.setBusinessUtilityBillImage(profileDTO.getBusinessUtilityBillPhoto());

        // Update bank details
        BankAccount bank = sp.getBankAccount();
        bank.setBankName(profileDTO.getBankAccount().getBankName());
        bank.setIfscCode(profileDTO.getBankAccount().getIfscCode());
        bank.setBankAccountNumber(profileDTO.getBankAccount().getBankAccountNumber());
        bank.setAccountHolderName(profileDTO.getBankAccount().getAccountHolderName());
        bankAccountRepository.save(bank);

        sp.setBankAccount(bank);
        serviceProviderRepository.save(sp);

        return "Service provider profile has been updated successfully.";
    }
}

