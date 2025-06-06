package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.*;
import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.FeedbackProvidersRepository;
import com.SmartLaundry.service.Customer.CacheService;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final FeedbackProvidersRepository feedbackRepo;
    private final CacheService cacheService;

    @Cacheable(value = "serviceProvidersCache", unless = "#result == null or #result.isEmpty()")
    public List<CustomerServiceProviderDTO> getAllServiceProvidersForCustomer() {
        List<ServiceProvider> providers = serviceProviderRepository.findAllWithUserAddresses();

        return providers.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    public CustomerServiceProviderDTO getServiceProviderById(String serviceProviderId) {
        Optional<ServiceProvider> spOpt = serviceProviderRepository.findByIdWithUserAddress(serviceProviderId);
        ServiceProvider sp = spOpt.orElseThrow(() -> new RuntimeException("Service provider not found with id: " + serviceProviderId));
        return convertToCustomerDTO(sp);
    }

    @CacheEvict(value = "serviceProvidersCache", allEntries = true)
    public ServiceProvider createServiceProvider(ServiceProvider newProvider) {
        return serviceProviderRepository.save(newProvider);
    }

    @CacheEvict(value = "serviceProvidersCache", allEntries = true)
    public ServiceProvider updateServiceProvider(String serviceProviderId, ServiceProvider updatedData) {
        Optional<ServiceProvider> sp = serviceProviderRepository.findById(serviceProviderId);

        if (sp.isEmpty()) {
            throw new RuntimeException("Service provider not found with id: " + serviceProviderId);
        }

        ServiceProvider existingSP = sp.get();
        existingSP.setBusinessName(updatedData.getBusinessName());
        existingSP.setPhotoImage(updatedData.getPhotoImage());

        if (updatedData.getBankAccount() != null) {
            existingSP.setBankAccount(updatedData.getBankAccount());
        }

        existingSP.getPrices().clear();
        existingSP.getPrices().addAll(updatedData.getPrices());

        if (updatedData.getUser() != null && updatedData.getUser().getAddress() != null) {
            List<UserAddress> updatedAddresses = updatedData.getUser().getAddress();
            existingSP.getUser().setAddress(updatedAddresses);
        }

        cacheService.clearServiceProvidersCache();
        return serviceProviderRepository.save(existingSP);
    }

    @CacheEvict(value = "serviceProvidersCache", allEntries = true)
    public void deleteServiceProvider(String serviceProviderId) {
        if (!serviceProviderRepository.existsById(serviceProviderId)) {
            throw new RuntimeException("Service provider not found with id: " + serviceProviderId);
        }
        serviceProviderRepository.deleteById(serviceProviderId);
        cacheService.clearServiceProvidersCache();
    }

    public CustomerServiceProviderDTO convertToCustomerDTO(ServiceProvider sp) {

        // Map prices to PriceDTO list
        List<PriceDTO> priceDTOs = convertPricesToDTO(sp.getPrices());

        // Extract unique items from prices and map to ItemDTO
        Set<ItemDTO> itemDTOs = sp.getPrices().stream()
                .map(price -> {
                    Items item = price.getItem();
                    return ItemDTO.builder()
                            .itemName(item.getItemName())
                            .serviceName(item.getService() != null ? item.getService().getServiceName() : null)
                            .subServiceName(item.getSubService() != null ? item.getSubService().getSubServiceName() : null)
                            .build();
                })
                .collect(Collectors.toSet()); // uses equals/hashCode to eliminate duplicates

        // Feedback ratings and reviews
        List<FeedbackProviders> feedbacks = feedbackRepo.findByServiceProvider_ServiceProviderId(sp.getServiceProviderId());
        List<ReviewDTO> reviews = feedbacks.stream()
                .filter(fb -> fb.getReview() != null && fb.getUser() != null)
                .map(fb -> new ReviewDTO(buildFullName(fb.getUser()), fb.getReview()))
                .collect(Collectors.toList());

        Long avgRating = (long) feedbacks.stream()
                .mapToInt(FeedbackProviders::getRating)
                .average()
                .orElse(0.0);

        return CustomerServiceProviderDTO.builder()
                .serviceProviderId(sp.getServiceProviderId())
                .businessName(sp.getBusinessName())
                .photoImage(sp.getPhotoImage())
                .address(getFirstAddress(sp.getUser()))
                .averageRating(avgRating)
                .reviews(reviews)
                .prices(priceDTOs)
                .items(new ArrayList<>(itemDTOs)) // convert Set to List
                .build();
    }

    private String buildFullName(Users user) {
        if (user == null) return "Anonymous";
        String fullName = Optional.ofNullable(user.getFirstName()).orElse("")
                + " " + Optional.ofNullable(user.getLastName()).orElse("");
        return fullName.trim().isEmpty() ? "Anonymous" : fullName.trim();
    }

    private AddressDTO getFirstAddress(Users user) {
        if (user == null || user.getAddress() == null || user.getAddress().isEmpty()) {
            return null;
        }

        return user.getAddress().stream()
                .filter(addr -> addr.getLatitude() != null && addr.getLongitude() != null)
                .findFirst()
                .map(this::mapAddress)
                .orElse(mapAddress(user.getAddress().get(0)));
    }

    private AddressDTO mapAddress(UserAddress address) {
        if (address == null) return null;

        CityDTO cityDTO = address.getCity() != null ? new CityDTO(address.getCity()) : null;

        return AddressDTO.builder()
                .name(address.getName())
                .areaName(address.getAreaName())
                .city(cityDTO)
                .pincode(address.getPincode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    private List<PriceDTO> convertPricesToDTO(List<Price> prices) {
        if (prices == null) return Collections.emptyList();

        return prices.stream()
                .map(price -> PriceDTO.builder()
                        .price(price.getPrice())
                        .item(new PriceDTO.ItemDTO(price.getItem().getItemId()))
                        .serviceProvider(null)
                        .build())
                .collect(Collectors.toList());
    }
}
