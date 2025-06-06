package com.SmartLaundry.service.ServiceProvider;

import com.SmartLaundry.dto.*;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.FeedbackProvidersRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;  // Injected instance
    private final FeedbackProvidersRepository feedbackRepo;

    @Cacheable(value = "serviceProvidersCache", unless = "#result == null or #result.isEmpty()")
    public List<CustomerServiceProviderDTO> getAllServiceProvidersForCustomer() {
        List<ServiceProvider> providers = serviceProviderRepository.findAll();

        return providers.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }
    @CacheEvict(value = "serviceProvidersCache", allEntries = true)
    public ServiceProvider updateServiceProvider(Long serviceProviderId, ServiceProvider updatedData) {
        Optional<ServiceProvider> sp = serviceProviderRepository.findById("123");  // ✅ correct


        if (sp.isEmpty()) {
            throw new RuntimeException("Service provider not found with id: " + serviceProviderId);
        }

        ServiceProvider existingSP = sp.get();

        // Update basic fields
        existingSP.setBusinessName(updatedData.getBusinessName());
//        existingSP.setPhoto(updatedData.getPhoto());

        // Update address if provided
//        if (updatedData.getAddress() != null) {
//            UserAddress updatedAddress = updatedData.getAddress();
//            UserAddress existingAddress = existingSP.getAddress();
//
//            if (existingAddress == null) {
////                existingSP.setAddress(updatedAddress);
//            } else {
//                existingAddress.setName(updatedAddress.getName());
//                existingAddress.setAreaName(updatedAddress.getAreaName());
//                existingAddress.setCity(updatedAddress.getCity());
//                existingAddress.setPincode(updatedAddress.getPincode());
//                existingAddress.setLatitude(updatedAddress.getLatitude());
//                existingAddress.setLongitude(updatedAddress.getLongitude());
//            }
//        }

        // Optional: update other related data (items, etc.)

        return serviceProviderRepository.save(existingSP);
    }



    public CustomerServiceProviderDTO convertToCustomerDTO(ServiceProvider sp) {
        // Convert Items → ItemDTO
//        List<ItemDTO> itemDTOs = sp.getItems().stream()
//                .map(item -> {
//                    ItemDTO dto = new ItemDTO();
//                    dto.setItemName(item.getItemName());
//                    dto.setServiceName(item.getService().getServiceName());
//                    dto.setSubServiceName(item.getSubService().getSubServiceName());
//                    return dto;
//                })
//                .collect(Collectors.toList());

        // Feedback ratings and reviews, now including user names
        List<FeedbackProviders> feedbacks = feedbackRepo.findByServiceProvider_ServiceProviderId(sp.getServiceProviderId());

        List<ReviewDTO> reviews = feedbacks.stream()
                .filter(fb -> fb.getReview() != null && fb.getUser() != null)
                .map(fb -> new ReviewDTO(
                        fb.getUser().getFirstName() != null ? fb.getUser().getFirstName() : "Anonymous",
                        fb.getReview()))
                .collect(Collectors.toList());


        Long avgRating = (long) feedbacks.stream()
                .mapToInt(FeedbackProviders::getRating)
                .average()
                .orElse(0.0);

        String userName = null;
        if (sp.getUser() != null) {
            userName = sp.getUser().getFirstName();  // or getUsername(), depending on your model
        }
        return CustomerServiceProviderDTO.builder()
                .serviceProviderId(sp.getServiceProviderId())
                .businessName(sp.getBusinessName())
//                .photoImage(sp.getPhoto())
//                .address(mapAddress(sp.getAddress()))
                .averageRating(avgRating)
                .reviews(reviews)
//                .items(itemDTOs)
                .userName(userName)
                .build();
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

}
