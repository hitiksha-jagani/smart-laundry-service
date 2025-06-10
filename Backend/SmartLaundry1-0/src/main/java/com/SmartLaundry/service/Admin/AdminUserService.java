package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.CustomerResponseDTO;
import com.SmartLaundry.dto.Admin.DeliveryAgentResponseDTO;
import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Admin.ServiceProviderResponseDTO;
import com.SmartLaundry.dto.DeliveryAgent.DeliveryAgentProfileDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @Cacheable(
            value = "customerTableCache",
            key = "T(java.util.Objects).toString(#keyword, '') + '-' + T(java.util.Objects).toString(#startDate, '') + '-' + T(java.util.Objects).toString(#endDate, '') + '-' + #sortBy"
    )
    public List<CustomerResponseDTO> getFilteredCustomers(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        Specification<Users> spec = UserSpecification.searchByEmailOrPhone(keyword)
                .and(UserSpecification.joinDateBetween(startDate, endDate))
                .and((root, query, cb) -> cb.equal(root.get("role"), UserRole.CUSTOMER));

        Sort sort = "joinDate".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "userId");

        List<Users> users = userRepository.findAll(spec, sort);

        return users.stream().map(this::mapToCustomerDTO).collect(Collectors.toList());
    }

    private CustomerResponseDTO mapToCustomerDTO(Users user) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhoneNo());
        dto.setEmail(user.getEmail());

        UserAddress userAddresses = userAddressRepository.findByUsers(user);

        CustomerResponseDTO.AddressDTO addressDTO = CustomerResponseDTO.AddressDTO.builder()
                .name(userAddresses.getName())
                .areaName(userAddresses.getAreaName())
                .pincode(userAddresses.getPincode())
                .cityName(userAddresses.getCity().getCityName())
                .build();

        dto.setAddresses(addressDTO);

        return dto;
    }

//    @Cacheable(
//            value = "serviceProviderTableCache",
//            key = "T(java.util.Objects).toString(#keyword, '') + '-' + T(java.util.Objects).toString(#startDate, '') + '-' + T(java.util.Objects).toString(#endDate, '') + '-' + #sortBy"
//    )
    @Transactional
    public List<ServiceProviderResponseDTO> getFilteredServiceProviders(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        Specification<Users> spec = UserSpecification.searchByEmailOrPhone(keyword)
                .and(UserSpecification.joinDateBetween(startDate, endDate))
                .and((root, query, cb) -> cb.equal(root.get("role"), UserRole.SERVICE_PROVIDER));

        Sort sort = "joinDate".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "userId");

        List<Users> users = userRepository.findAll(spec, sort);

        return users.stream().map(this::mapToServiceProviderDTO).collect(Collectors.toList());
    }

    private ServiceProviderResponseDTO mapToServiceProviderDTO(Users user) {
        ServiceProviderResponseDTO dto = new ServiceProviderResponseDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhoneNo());
        dto.setEmail(user.getEmail());

        ServiceProvider serviceProvider = serviceProviderRepository.findByUser(user);

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
                        .bankAccountNumber(serviceProvider.getBankAccount().getBankAccountNumber())
                        .bankName(serviceProvider.getBankAccount().getBankName())
                        .accountHolderName(serviceProvider.getBankAccount().getAccountHolderName())
                        .ifscCode(serviceProvider.getBankAccount().getIfscCode())
                        .build();

        dto.setBusinessName(serviceProvider.getBusinessName());
        dto.setBusinessLicenseNumber(serviceProvider.getBusinessLicenseNumber());
        dto.setGstNumber(serviceProvider.getGstNumber());
        dto.setNeedOfDeliveryAgent(serviceProvider.getNeedOfDeliveryAgent());
        dto.setSchedulePlans(serviceProvider.getSchedulePlans());
        dto.setBankAccount(bankAccountDTO);
        dto.setAadharCardPhoto("/image/aadhar/" + user.getUserId());
        dto.setProfilePhoto("/image/profile/" + user.getUserId());
        dto.setPanCardPhoto("/image/pan/" + user.getUserId());
        dto.setBusinessUtilityBillPhoto("/image/utilitybill/" + user.getUserId());
        dto.setPriceDTO(priceDTOs);
        dto.setAddresses(addressDTO);

        return dto;
    }

    @Transactional
    public List<DeliveryAgentResponseDTO> getFilteredDeliveryAgents(String keyword, LocalDate startDate, LocalDate endDate, String sortBy) {
        Specification<Users> spec = UserSpecification.searchByEmailOrPhone(keyword)
                .and(UserSpecification.joinDateBetween(startDate, endDate))
                .and((root, query, cb) -> cb.equal(root.get("role"), UserRole.DELIVERY_AGENT));

        Sort sort = "joinDate".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.ASC, "userId");

        List<Users> users = userRepository.findAll(spec, sort);

        return users.stream().map(this::mapToDeliveryAgentDTO).collect(Collectors.toList());
    }

    private DeliveryAgentResponseDTO mapToDeliveryAgentDTO(Users user) {
        DeliveryAgentResponseDTO dto = new DeliveryAgentResponseDTO();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhoneNo());
        dto.setEmail(user.getEmail());

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user).orElseThrow();

        UserAddress userAddresses = userAddressRepository.findByUsers(user);

        DeliveryAgentResponseDTO.AddressDTO addressDTO = DeliveryAgentResponseDTO.AddressDTO.builder()
                .name(userAddresses.getName())
                .areaName(userAddresses.getAreaName())
                .pincode(userAddresses.getPincode())
                .cityName(userAddresses.getCity().getCityName())
                .build();

        dto.setAccountHolderName(deliveryAgent.getAccountHolderName());
        dto.setBankAccountNumber(deliveryAgent.getBankAccountNumber());
        dto.setBankName(deliveryAgent.getBankName());
        dto.setDateOfBirth(deliveryAgent.getDateOfBirth());
        dto.setGender(deliveryAgent.getGender());
        dto.setIfscCode(deliveryAgent.getIfscCode());
        dto.setVehicleNumber(deliveryAgent.getVehicleNumber());
        dto.setAadharCardPhoto("/image/aadhar/" + user.getUserId());
        dto.setPanCardPhoto("/image/pan/" + user.getUserId());
        dto.setProfilePhoto("/image/profile/" + user.getUserId());
        dto.setDrivingLicensePhoto("/image/license/" + user.getUserId());
        dto.setAddress(addressDTO);

        return dto;
    }



}
