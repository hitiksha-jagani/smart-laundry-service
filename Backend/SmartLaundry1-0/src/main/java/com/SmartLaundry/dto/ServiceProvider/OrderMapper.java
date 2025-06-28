package com.SmartLaundry.dto.ServiceProvider;


import com.SmartLaundry.dto.Customer.OrderResponseDto;
import com.SmartLaundry.model.BillStatus;
import com.SmartLaundry.model.BookingItem;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.OtpPurpose;
import com.SmartLaundry.repository.BookingItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final BookingItemRepository bookingItemRepository;

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

        // ✅ Determine bill status
        BillStatus billStatus = (order.getBill() != null)
                ? order.getBill().getStatus()
                : BillStatus.PENDING_FOR_PAYMENT;

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
                .bookingItems(itemDtos)
                .billStatus(billStatus);

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



    public OrderResponseDto toOtpVerificationDto(Order order) {
        if (order == null) return null;

        String userId = order.getUsers() != null ? order.getUsers().getUserId() : null;
        String serviceProviderId = order.getServiceProvider() != null ? order.getServiceProvider().getServiceProviderId() : null;
        String customerName = order.getUsers() != null
                ? order.getUsers().getFirstName() + " " + order.getUsers().getLastName()
                : "Customer";
        boolean requiresPickupOtp = order.getStatus().name().equals("IN_CLEANING");
        boolean requiresDeliveryOtp = order.getStatus().name().equals("OUT_FOR_DELIVERY");

        return OrderResponseDto.builder()
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
                .customerName(customerName)
                .requiresPickupOtp(requiresPickupOtp)
                .requiresDeliveryOtp(requiresDeliveryOtp)
                .agentId(order.getPickupDeliveryAgent() != null
                        ? order.getPickupDeliveryAgent().getDeliveryAgentId()
                        : null)
                .providerId(serviceProviderId)
                .build();
    }

    public List<ActiveOrderGroupedDto> groupOrdersByOrderId(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return Collections.emptyList();

        return orders.stream().map(order -> {
            List<ActiveOrderDto> itemDtos = order.getBookingItems().stream()
                    .map(b -> ActiveOrderDto.builder()
                            .itemName(b.getItem().getItemName())
                            .quantity(b.getQuantity())
                            .service(b.getItem().getService().getServiceName())
                            .subService(b.getItem().getSubService() != null
                                    ? b.getItem().getSubService().getSubServiceName()
                                    : null)
                            .build())
                    .toList();

            return ActiveOrderGroupedDto.builder()
                    .orderId(order.getOrderId())
                    .pickupDate(order.getPickupDate())
                    .pickupTime(order.getPickupTime())
                    .status(order.getStatus())
                    .items(itemDtos)
                    .build();

        }).toList();
    }
}