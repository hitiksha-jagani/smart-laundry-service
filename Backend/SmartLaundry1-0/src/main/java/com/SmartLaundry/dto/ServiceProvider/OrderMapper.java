package com.SmartLaundry.dto.ServiceProvider;


import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.model.BookingItem;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.repository.BookingItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final BookingItemRepository bookingItemRepository;

//    public OrderResponseDto toOrderResponseDto(Order order) {
//        List<OrderResponseDto.BookingItemDto> bookingItems = bookingItemRepository
//                .findByOrder(order)
//                .stream()
//                .map(b -> OrderResponseDto.BookingItemDto.builder()
//                        .itemId(b.getItem().getItemId())
//                        .itemName(b.getItem().getItemName())
//                        .quantity(b.getQuantity())
//                        .finalPrice(b.getFinalPrice())
//                        .build())
//                .toList();
//
//        OrderResponseDto.SchedulePlanDto planDto = null;
//        if (order.getOrderSchedulePlan() != null) {
//            planDto = OrderResponseDto.SchedulePlanDto.builder()
//                    .plan(order.getOrderSchedulePlan().getSchedulePlan().name())
//                    .payEachDelivery(order.getOrderSchedulePlan().isPayEachDelivery())
//                    .payLastDelivery(order.getOrderSchedulePlan().isPayLastDelivery())
//                    .build();
//        }
//
//        return OrderResponseDto.builder()
//                .orderId(order.getOrderId())
//                .userId(order.getUsers().getUserId())
//                .serviceProviderId(order.getServiceProvider().getServiceProviderId())
//                .contactName(order.getContactName())
//                .contactPhone(order.getContactPhone())
//                .contactAddress(order.getContactAddress())
//                .latitude(order.getLatitude())
//                .longitude(order.getLongitude())
//                .pickupDate(order.getPickupDate())
//                .pickupTime(order.getPickupTime())
//                .status(order.getStatus())
//                .bookingItems(bookingItems)
//                .schedulePlan(planDto)
//                .build();
//    }

    public OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null) return null;

        String userId = order.getUsers() != null ? order.getUsers().getUserId() : null;
        String serviceProviderId = order.getServiceProvider() != null ? order.getServiceProvider().getServiceProviderId() : null;

        List<OrderResponseDto.BookingItemDto> itemDtos = order.getBookingItems() == null ? Collections.emptyList() :
                order.getBookingItems().stream()
                        .map(b -> OrderResponseDto.BookingItemDto.builder()
                                .itemId(b.getItem().getItemId())
                                .itemName(b.getItem().getItemName())
                                .quantity(b.getQuantity())
                                .finalPrice(b.getFinalPrice())
                                .build())
                        .toList();

        OrderResponseDto.OrderResponseDtoBuilder builder = OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .userId(userId)
                .serviceProviderId(serviceProviderId)
                .contactName(order.getContactName())
                .contactPhone(order.getContactPhone())
                .contactAddress(order.getContactAddress())
                .latitude(order.getLatitude())
                .longitude(order.getLongitude())
                .pickupDate(order.getPickupDate())
                .pickupTime(order.getPickupTime())
                .status(order.getStatus())
                .bookingItems(itemDtos);

        if (order.getOrderSchedulePlan() != null) {
            OrderResponseDto.SchedulePlanDto schedulePlanDto = OrderResponseDto.SchedulePlanDto.builder()
                    .plan(order.getOrderSchedulePlan().getSchedulePlan().name())
                    .payEachDelivery(order.getOrderSchedulePlan().isPayEachDelivery())
                    .payLastDelivery(order.getOrderSchedulePlan().isPayLastDelivery())
                    .build();
            builder.schedulePlan(schedulePlanDto);
        }

        return builder.build();
    }


}

