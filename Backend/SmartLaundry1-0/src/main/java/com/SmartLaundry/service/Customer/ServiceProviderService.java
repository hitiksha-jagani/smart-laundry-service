package com.SmartLaundry.service.Customer;
//All OR Nearby Service ProviderList
import com.SmartLaundry.dto.*;
import com.SmartLaundry.dto.Admin.PriceDTO;
import com.SmartLaundry.dto.Customer.BillResponseDto;
import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.FeedbackProvidersRepository;

//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final FeedbackProvidersRepository feedbackRepo;
    private final CacheService cacheService;
    private final StringRedisTemplate redisTemplate;
    @Transactional
    // @Cacheable(value = "serviceProvidersCache", unless = "#result == null or #result.isEmpty()")
    public List<CustomerServiceProviderDTO> getAllServiceProvidersForCustomer() {
        List<ServiceProvider> providers = serviceProviderRepository.findAllWithUserAddresses();

        return providers.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProviderById(String providerId) {
        // Fetch the provider to get the associated userId
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service Provider not found with ID: " + providerId));

        String userId = provider.getUser().getUserId(); // GeoRedis uses this as the Redis key

        // Delete from DB
        serviceProviderRepository.deleteById(providerId);

        // Remove from Redis Geo data
        redisTemplate.opsForGeo().remove("service_providers_geo", userId);

        System.out.println("🗑️ Removed provider " + providerId + " and Redis geo key " + userId);
    }


    @Transactional(readOnly = true)
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


@Transactional(readOnly = true)
    public CustomerServiceProviderDTO convertToCustomerDTO(ServiceProvider sp) {
        // Build PriceDTO list with full item info
        List<PriceDTO> priceDTOs = sp.getPrices().stream()
                .filter(p -> p.getItem() != null)
//                .map(price -> {
//                    Items item = price.getItem();
                .map(price -> {
                    Items item = price.getItem();
                    if (item == null) {
                        System.out.println("❌ Null item found for price ID: " + price.getPriceId());
                        return null; // skip this item safely
                    }

                    return PriceDTO.builder()
                            .price(price.getPrice())
                            .item(PriceDTO.ItemDTO.builder()
                                    .itemId(item.getItemId())
                                    .itemName(item.getItemName())
                                    .serviceId(item.getService() != null ? item.getService().getServiceId() : null)
                                    .serviceName(item.getService() != null ? item.getService().getServiceName() : null)
                                    .subServiceId(item.getSubService() != null ? item.getSubService().getSubServiceId() : null)
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

        Double avg = feedbackRepo.findAverageRatingByServiceProvider(sp.getServiceProviderId());
        Double avgRating = avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;

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

//    private String convertFilePathToPublicUrl(String absolutePath) {
//        if (absolutePath == null || absolutePath.isEmpty()) return null;
//
//        String baseDir = "/app/images/";
//
//        if (absolutePath.startsWith(baseDir)) {
//            String relativePath = absolutePath.substring(baseDir.length()).replace("\\", "/");
//            return "http://localhost:8080/images/" + relativePath;
//        }
//
//        return "http://localhost:8080/images/default-provider.jpg";
//    }


    private String convertFilePathToPublicUrl(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            // Return a default image URL from your Cloudinary account or public URL
            return "https://res.cloudinary.com/dot327hzh/image/upload/v1691675380/default-provider.jpg";
        }
        return cloudinaryUrl;
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
