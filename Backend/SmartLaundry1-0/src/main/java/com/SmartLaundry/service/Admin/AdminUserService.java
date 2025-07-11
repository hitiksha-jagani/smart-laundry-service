package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.*;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentProfileDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);

    public CustomerGraphOverviewDTO getGraphsForUsers(UserRole role) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // 1. Monthly trend for current year
        List<CustomerGraphOverviewDTO.MonthlyUserTrendDTO> monthlyTrend = new ArrayList<>();
        for (int m = 1; m <= currentMonth; m++) {
            LocalDateTime start = LocalDate.of(currentYear, m, 1).atStartOfDay();
            LocalDateTime end = start.plusMonths(1).minusNanos(1);
            long count = userRepository.countByRoleAndCreatedAtBetween(role, start, end);
            String monthLabel = Month.of(m).name().substring(0, 3); // "JAN", "FEB", etc.
            monthlyTrend.add(new CustomerGraphOverviewDTO.MonthlyUserTrendDTO(monthLabel, count));
        }

        // 2. Daily trend for current month
        int daysInMonth = today.getDayOfMonth();
        List<CustomerGraphOverviewDTO.DailyUserTrendDTO> dailyTrend = new ArrayList<>();
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDateTime start = LocalDate.of(currentYear, currentMonth, d).atStartOfDay();
            LocalDateTime end = start.plusDays(1).minusNanos(1);
            long count = userRepository.countByRoleAndCreatedAtBetween(role, start, end);
            dailyTrend.add(new CustomerGraphOverviewDTO.DailyUserTrendDTO(d, count));
        }

        // 3. Yearly growth
        List<CustomerGraphOverviewDTO.YearlyUserCountDTO> yearlyTrend = new ArrayList<>();
        for (int y = currentYear - 4; y <= currentYear; y++) {
            LocalDateTime start = LocalDate.of(y, 1, 1).atStartOfDay();
            LocalDateTime end = LocalDate.of(y, 12, 31).atTime(23, 59, 59);
            long count = userRepository.countByRoleAndCreatedAtBetween(role, start, end);
            yearlyTrend.add(new CustomerGraphOverviewDTO.YearlyUserCountDTO(y, count));
        }

        // 4. Region-wise customer count by city
        List<Object[]> raw = userRepository.countCustomersGroupedByCity(role);
        List<CustomerGraphOverviewDTO.RegionUserDistributionDTO> regionWise = raw.stream()
                .map(r -> new CustomerGraphOverviewDTO.RegionUserDistributionDTO((String) r[0], (Long) r[1]))
                .toList();

        return CustomerGraphOverviewDTO.builder()
                .usersThisYearMonthlyTrend(monthlyTrend)
                .newUsersThisMonthDailyTrend(dailyTrend)
                .userGrowthTrend(yearlyTrend)
                .regionWiseDistribution(regionWise)
                .build();
    }

    public List<CustomerResponseDTO> getFilteredCustomers(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        Specification<Users> spec = null;

        // Always filter by customer role
        spec = addSpec(spec, (root, query, cb) -> cb.equal(root.get("role"), UserRole.CUSTOMER));

        if (keyword != null && !keyword.isBlank()) {
            spec = addSpec(spec, UserSpecification.searchByEmailOrPhone(keyword));
        }

        // Apply date filter only if either is not null
        if (startDate != null || endDate != null) {
            if (startDate == null) startDate = LocalDate.of(2025, 5, 1);
            if (endDate == null) endDate = LocalDate.now();
            spec = addSpec(spec, UserSpecification.joinDateBetween(startDate, endDate));
        }

        // Default sort by userId
        Sort sort = "joinDate".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "userId");

        List<Users> users = userRepository.findAll(spec, sort);

        return users.stream().map(this::mapToCustomerDTO).collect(Collectors.toList());
    }

    private CustomerResponseDTO mapToCustomerDTO(Users user) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhoneNo());
        dto.setEmail(user.getEmail());
        dto.setJoinAt(user.getCreatedAt());

        UserAddress address = user.getAddress();

        if (address != null) {
            CustomerResponseDTO.AddressDTO addressDTO = CustomerResponseDTO.AddressDTO.builder()
                    .name(address.getName())
                    .areaName(address.getAreaName())
                    .pincode(address.getPincode())
                    .cityName(address.getCity().getCityName())
                    .build();

            dto.setAddresses(addressDTO);
        }

        return dto;
    }

    private <T> Specification<T> addSpec(Specification<T> base, Specification<T> toAdd) {
        return base == null ? toAdd : base.and(toAdd);
    }


//    @Cacheable(
//            value = "serviceProviderTableCache",
//            key = "T(java.util.Objects).toString(#keyword, '') + '-' + T(java.util.Objects).toString(#startDate, '') + '-' + T(java.util.Objects).toString(#endDate, '') + '-' + #sortBy"
//    )
    @Transactional
    public List<ServiceProviderResponseDTO> getFilteredServiceProviders(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {

        Specification<Users> spec = (root, query, cb) -> cb.equal(root.get("role"), UserRole.SERVICE_PROVIDER);

        // Always filter by customer role
//        spec = addSpec(spec, (root, query, cb) -> cb.equal(root.get("role"), UserRole.SERVICE_PROVIDER));

        if (keyword != null && !keyword.isBlank()) {
            spec = addSpec(spec, UserSpecification.searchByEmailOrPhone(keyword));
        }

        // Apply date filter only if either is not null
        if (startDate != null || endDate != null) {
            if (startDate == null) startDate = LocalDate.of(2025, 5, 1);
            if (endDate == null) endDate = LocalDate.now();
            spec = addSpec(spec, UserSpecification.joinDateBetween(startDate, endDate));
        }

        Sort sort = "joinDate".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "userId");

        List<Users> users = userRepository.findAll(spec, sort);

        return users.stream().map(this::mapToServiceProviderDTO).collect(Collectors.toList());
    }

    private ServiceProviderResponseDTO mapToServiceProviderDTO(Users user) {
        ServiceProviderResponseDTO dto = new ServiceProviderResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhoneNo());
        dto.setEmail(user.getEmail());
        dto.setJoinedAt(user.getCreatedAt());

        ServiceProvider serviceProvider = serviceProviderRepository.getByUser(user)
                .orElse(null);
        if (serviceProvider == null) {
            return null;
        }

        List<Price> prices = priceRepository.findByServiceProvider(serviceProvider);

        List<ServiceProviderResponseDTO.priceDTO> priceDTOs = prices.stream()
                .map(price -> {
                    ServiceProviderResponseDTO.priceDTO pdto = new ServiceProviderResponseDTO.priceDTO();
                    pdto.setItemId(price.getItem().getItemId());
                    pdto.setPrice(price.getPrice());
                    pdto.setServiceProviderId(price.getServiceProvider().getServiceProviderId());
                    return pdto;
                })
                .collect(Collectors.toList());

        UserAddress userAddresses = userAddressRepository.findByUsers(user);

        ServiceProviderResponseDTO.AddressDTO addressDTO = ServiceProviderResponseDTO.AddressDTO.builder()
                .name(userAddresses.getName())
                .areaName(userAddresses.getAreaName())
                .pincode(userAddresses.getPincode())
                .cityName(userAddresses.getCity().getCityName())
                .build();

        ServiceProviderResponseDTO.BankAccountDTO bankAccountDTO = ServiceProviderResponseDTO.BankAccountDTO.builder()
                        .bankAccountNumber(serviceProvider.getBankAccount() != null ? serviceProvider.getBankAccount().getBankAccountNumber() : null)
                        .bankName(serviceProvider.getBankAccount() != null ? serviceProvider.getBankAccount().getBankName() : null)
                        .accountHolderName(serviceProvider.getBankAccount() != null ? serviceProvider.getBankAccount().getAccountHolderName() : null)
                        .ifscCode(serviceProvider.getBankAccount() != null ? serviceProvider.getBankAccount().getIfscCode() : null)
                        .build();

        dto.setServiceProviderId(serviceProvider.getServiceProviderId());
        dto.setBusinessName(serviceProvider.getBusinessName());
        dto.setBusinessLicenseNumber(serviceProvider.getBusinessLicenseNumber());
        dto.setGstNumber(serviceProvider.getGstNumber());
        dto.setNeedOfDeliveryAgent(serviceProvider.getNeedOfDeliveryAgent());
        dto.setSchedulePlans(serviceProvider.getSchedulePlans());
        dto.setBankAccount(bankAccountDTO);
        dto.setAadharCardPhoto(serviceProvider.getAadharCardImage() != null ? "/image/provider/aadhar/" + user.getUserId() : null);
        dto.setProfilePhoto(serviceProvider.getPhotoImage() != null ? "/image/provider/profile/" + user.getUserId() : null);
        dto.setPanCardPhoto(serviceProvider.getPanCardImage() != null ? "/image/provider/pan/" + user.getUserId() : null);
        dto.setBusinessUtilityBillPhoto(serviceProvider.getBusinessUtilityBillImage() != null ? "/image/provider/utilitybill/" + user.getUserId() : null);
        dto.setPriceDTO(priceDTOs);
        dto.setAddresses(addressDTO);

        return dto;
    }

    @Transactional
    public List<DeliveryAgentResponseDTO> getFilteredDeliveryAgents(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {

        Specification<Users> spec = (root, query, cb) -> cb.equal(root.get("role"), UserRole.DELIVERY_AGENT);;

        // Always filter by customer role
//        spec = addSpec(spec, (root, query, cb) -> cb.equal(root.get("role"), UserRole.DELIVERY_AGENT));

        if (keyword != null && !keyword.isBlank()) {
            spec = addSpec(spec, UserSpecification.searchByEmailOrPhone(keyword));
        }

        // Apply date filter only if either is not null
        if (startDate != null || endDate != null) {
            if (startDate == null) startDate = LocalDate.of(2025, 5, 1);
            if (endDate == null) endDate = LocalDate.now();
            spec = addSpec(spec, UserSpecification.joinDateBetween(startDate, endDate));
        }

        Sort sort = "joinDate".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "userId");

        List<Users> users = userRepository.findAll(spec, sort);

        // Log for debugging
        users.forEach(u -> System.out.println(u.getUserId() + " | " + u.getFirstName() + " | " + u.getRole()));

        return users.stream().map(this::mapToDeliveryAgentDTO).collect(Collectors.toList());
    }

    private DeliveryAgentResponseDTO mapToDeliveryAgentDTO(Users user) {
        DeliveryAgentResponseDTO dto = new DeliveryAgentResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhoneNo());
        dto.setEmail(user.getEmail());
        dto.setJoinedAt(user.getCreatedAt());

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElse(null);
        if(deliveryAgent == null) {
            return null;
        }

        UserAddress userAddresses = userAddressRepository.findByUsers(user);
        DeliveryAgentResponseDTO.AddressDTO addressDTO = new DeliveryAgentResponseDTO.AddressDTO();
        if(userAddresses == null) {
            addressDTO = null;
        } else {
           addressDTO = DeliveryAgentResponseDTO.AddressDTO.builder()
                    .name(userAddresses.getName())
                    .areaName(userAddresses.getAreaName())
                    .pincode(userAddresses.getPincode())
                    .cityName(userAddresses.getCity().getCityName())
                    .build();
        }

        dto.setDeliveryAgentId(deliveryAgent.getDeliveryAgentId());
        dto.setAccountHolderName(deliveryAgent.getAccountHolderName());
        dto.setBankAccountNumber(deliveryAgent.getBankAccountNumber());
        dto.setBankName(deliveryAgent.getBankName());
        dto.setDateOfBirth(deliveryAgent.getDateOfBirth());
        dto.setGender(deliveryAgent.getGender());
        dto.setIfscCode(deliveryAgent.getIfscCode());
        dto.setVehicleNumber(deliveryAgent.getVehicleNumber());
        dto.setAadharCardPhoto(deliveryAgent.getAadharCardPhoto() != null ? "/image/agent/aadhar/" + user.getUserId() : null);
        dto.setPanCardPhoto(deliveryAgent.getPanCardPhoto() != null ? "/image/agent/pan/" + user.getUserId() : null);
        dto.setProfilePhoto(deliveryAgent.getProfilePhoto() != null ? "/image/agent/profile/" + user.getUserId() : null);
        dto.setDrivingLicensePhoto(deliveryAgent.getDrivingLicensePhoto() != null ? "/image/agent/license/" + user.getUserId() : null);
        dto.setAddress(addressDTO);

        return dto;
    }

    public void toggleUserBlockStatus(String userId, boolean block) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        boolean blockStatus = user.isBlocked();

        if(blockStatus == block) {
            if(blockStatus == true) {
                throw new RuntimeException("User already blocked");
            } else {
                throw new RuntimeException("User already unblocked");
            }
        }

        user.setBlocked(block);
        userRepository.save(user);

    }

    public void deleteCustomer(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Prevent admin from deleting themselves or other admins
        if (user.getRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Cannot delete an admin user.");
        }

        userRepository.delete(user);
    }

    public void deleteServiceProvider(Users user, String providerId) {

        ServiceProvider serviceProvider = serviceProviderRepository.findById(providerId)
                .orElse(null);
        serviceProviderRepository.delete(serviceProvider);
        userRepository.delete(user);
    }

    public void deleteDeliveryAgent(Users user, String agentId) {

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findById(agentId)
                .orElse(null);
        deliveryAgentRepository.delete(deliveryAgent);
        userRepository.delete(user);
    }
}
