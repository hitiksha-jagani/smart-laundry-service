package com.SmartLaundry.service.Customer;
//All OR Nearby Service ProviderList
import com.SmartLaundry.dto.*;
import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Customer.BillResponseDto;
import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.FeedbackProvidersRepository;

import lombok.RequiredArgsConstructor;
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

   // @Cacheable(value = "serviceProvidersCache", unless = "#result == null or #result.isEmpty()")
    public List<CustomerServiceProviderDTO> getAllServiceProvidersForCustomer() {
        List<ServiceProvider> providers = serviceProviderRepository.findAllWithUserAddresses();

        return providers.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

//    public CustomerServiceProviderDTO getServiceProviderById(String serviceProviderId) {
//        Optional<ServiceProvider> spOpt = serviceProviderRepository.findByIdWithUserAddress(serviceProviderId);
//        ServiceProvider sp = spOpt.orElseThrow(() -> new RuntimeException("Service provider not found with id: " + serviceProviderId));
//        return convertToCustomerDTO(sp);
//    }

    public CustomerServiceProviderDTO getServiceProviderById(String serviceProviderId) {
        try {
            System.out.println("Fetching provider with ID: " + serviceProviderId);
            Optional<ServiceProvider> spOpt = serviceProviderRepository.findByIdWithUserAddress(serviceProviderId);
            ServiceProvider sp = spOpt.orElseThrow(() -> new RuntimeException("Service provider not found with id: " + serviceProviderId));
            return convertToCustomerDTO(sp);
        } catch (Exception e) {
            System.err.println("ERROR in getServiceProviderById: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    //    public CustomerServiceProviderDTO convertToCustomerDTO(ServiceProvider sp) {
//
//        // Map prices to PriceDTO list
//        List<PriceDTO> priceDTOs = convertPricesToDTO(sp.getPrices());
//
//        // Extract unique items from prices and map to ItemDTO
//        Set<ItemDTO> itemDTOs = sp.getPrices().stream()
//                .map(price -> {
//                    Items item = price.getItem();
//                    return ItemDTO.builder()
//                            .itemName(item.getItemName())
//                            .serviceName(item.getService() != null ? item.getService().getServiceName() : null)
//                            .subServiceName(item.getSubService() != null ? item.getSubService().getSubServiceName() : null)
//                            .build();
//                })
//                .collect(Collectors.toSet()); // uses equals/hashCode to eliminate duplicates
//
//        // Feedback ratings and reviews
//        List<FeedbackProviders> feedbacks = feedbackRepo.findByServiceProvider_ServiceProviderId(sp.getServiceProviderId());
//        List<ReviewDTO> reviews = feedbacks.stream()
//                .filter(fb -> fb.getReview() != null && fb.getUser() != null)
//                .map(fb -> new ReviewDTO(buildFullName(fb.getUser()), fb.getReview()))
//                .collect(Collectors.toList());
//
//        Long avgRating = (long) feedbacks.stream()
//                .mapToInt(FeedbackProviders::getRating)
//                .average()
//                .orElse(0.0);
//
//        return CustomerServiceProviderDTO.builder()
//                .serviceProviderId(sp.getServiceProviderId())
//                .businessName(sp.getBusinessName())
//                .photoImage(convertFilePathToPublicUrl(sp.getPhotoImage()))
//                .address(getFirstAddress(sp.getUser()))
//                .averageRating(avgRating)
//                .reviews(reviews)
//                .prices(priceDTOs)
//                .items(new ArrayList<>(itemDTOs)) // convert Set to List
//                .build();
//    }
    public CustomerServiceProviderDTO convertToCustomerDTO(ServiceProvider sp) {
        // Build PriceDTO list with full item info
        List<PriceDTO> priceDTOs = sp.getPrices().stream()
                .filter(p -> p.getItem() != null)
                .map(price -> {
                    Items item = price.getItem();
                    return PriceDTO.builder()
                            .price(price.getPrice())
                            .item(PriceDTO.ItemDTO.builder()
                                    .itemId(item.getItemId())
                                    .itemName(item.getItemName())
                                    .serviceName(item.getService() != null ? item.getService().getServiceName() : null)
                                    .subServiceName(item.getSubService() != null ? item.getSubService().getSubServiceName() : null)
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        // Reviews and ratings
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
                .photoImage(convertFilePathToPublicUrl(sp.getPhotoImage()))
                .address(getFirstAddress(sp.getUser()))
                .averageRating(avgRating)
                .reviews(reviews)
                .prices(priceDTOs)
                .build();
    }



    private String convertFilePathToPublicUrl(String absolutePath) {
    if (absolutePath == null || absolutePath.isEmpty()) return null;

    String baseDir = "D:\\MSCIT\\summerinternship\\images\\";

    if (absolutePath.startsWith(baseDir)) {
        String relativePath = absolutePath.substring(baseDir.length()).replace("\\", "/");
        return "http://localhost:8080/images/" + relativePath;
    }

    return "http://localhost:8080/images/default-provider.jpg";
}

    private String buildFullName(Users user) {
        if (user == null) return "Anonymous";
        String fullName = Optional.ofNullable(user.getFirstName()).orElse("")
                + " " + Optional.ofNullable(user.getLastName()).orElse("");
        return fullName.trim().isEmpty() ? "Anonymous" : fullName.trim();
    }

    private AddressDTO getFirstAddress(Users user) {
        if (user == null || user.getAddress() == null) {
            return null;
        }

        UserAddress address = user.getAddress();
        if (address.getLatitude() != null && address.getLongitude() != null) {
            return mapAddress(address);
        }

        return null;
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

//    private List<PriceDTO> convertPricesToDTO(List<Price> prices) {
//        if (prices == null) return Collections.emptyList();
//
//        return prices.stream()
//                .map(price -> PriceDTO.builder()
//                        .price(price.getPrice())
//                        .item(new PriceDTO.ItemDTO(price.getItem().getItemId()))
//                        .serviceProvider(null)
//                        .build())
//                .collect(Collectors.toList());
//    }
}
