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
        if (user == null || user.getAddress() == null) {
            return null;
        }

        UserAddress address = user.getAddress();
        if (address.getLatitude() != null && address.getLongitude() != null) {
            return mapAddress(address);
        }

        return null;
    }
//    public OrderResponseDto buildOrderResponseDto(Order order) {
//        OrderResponseDto.OrderResponseDtoBuilder builder = OrderResponseDto.builder();
//
//        builder
//                .orderId(order.getOrderId())
//                .userId(order.getUser().getUserId())
//                .serviceProviderId(order.getServiceProvider().getServiceProviderId())
//                .contactName(order.getContactName())
//                .contactPhone(order.getContactPhone())
//                .contactAddress(order.getContactAddress())
//                .latitude(order.getLatitude() != null ? order.getLatitude() : 0.0)
//                .longitude(order.getLongitude() != null ? order.getLongitude() : 0.0)
//                .pickupDate(order.getPickupDate())
//                .pickupTime(order.getPickupTime())
//                .status(order.getStatus())
//                .needOfDeliveryAgent(order.getNeedOfDeliveryAgent());
//
//        // Booking items
//        if (order.getBookingItems() != null) {
//            List<OrderResponseDto.BookingItemDto> items = order.getBookingItems().stream()
//                    .map(item -> OrderResponseDto.BookingItemDto.builder()
//                            .itemId(item.getItem().getItemId())
//                            .itemName(item.getItem().getItemName())
//                            .quantity(item.getQuantity())
//                            .finalPrice(item.getFinalPrice())
//                            .build())
//                    .toList();
//            builder.bookingItems(items);
//        }
//
//        // Schedule plan
//        if (order.getOrderSchedulePlan() != null) {
//            builder.schedulePlan(OrderResponseDto.SchedulePlanDto.builder()
//                    .plan(order.getOrderSchedulePlan())
//                    .payEachDelivery(order.getOrderSchedulePlan().isPayEachDelivery())
//                    .payLastDelivery(order.getOrderSchedulePlan().isPayLastDelivery())
//                    .build());
//        }
//
//        return builder.build();
//    }


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
