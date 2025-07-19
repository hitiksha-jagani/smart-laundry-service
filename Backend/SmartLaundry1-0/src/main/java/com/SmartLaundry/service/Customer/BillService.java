package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.OrderSummaryDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BillService {

    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final BillRepository billRepository;
    @Autowired
    private final PriceRepository priceRepository;

    public Bill findById(String billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
    }

    public OrderSummaryDto getBillSummary(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Bill bill = billRepository.findByOrder(order);
        if (bill == null) {
            throw new IllegalStateException("No bill found for this order.");
        }

        // Use booking items from bill if available, else fallback to order's booking items
        List<BookingItem> itemsToUse = (bill.getBookingItems() == null || bill.getBookingItems().isEmpty())
                ? order.getBookingItems()
                : bill.getBookingItems();

        List<OrderSummaryDto.ItemSummary> itemSummaries = itemsToUse.stream().map(item -> {
            Items itemEntity = item.getItem();
            Long pricePerUnit = priceRepository
                    .findByServiceProviderAndItem(order.getServiceProvider(), itemEntity)
                    .map(Price::getPrice)
                    .orElse(0L);

            return OrderSummaryDto.ItemSummary.builder()
                    .itemName(Optional.ofNullable(itemEntity.getItemName()).orElse("Unnamed Item"))
                    .quantity(item.getQuantity())
                    .price(pricePerUnit.doubleValue())
                    .finalPrice(item.getFinalPrice())
                    .build();
        }).toList();

        // Extract service and subservice name
        String serviceName = "";
        String subServiceName = "";
        if (!itemsToUse.isEmpty()) {
            Items firstItem = Optional.ofNullable(itemsToUse.get(0)).map(BookingItem::getItem).orElse(null);
            if (firstItem != null) {
                SubService subService = firstItem.getSubService();
                Services service = subService != null ? subService.getServices() : firstItem.getService();

                subServiceName = Optional.ofNullable(subService)
                        .map(SubService::getSubServiceName)
                        .orElse("N/A");

                serviceName = Optional.ofNullable(service)
                        .map(Services::getServiceName)
                        .orElse("N/A");
            }
        }

        PaymentStatus paymentStatus = (bill.getPayment() != null && bill.getPayment().getStatus() != null)
                ? bill.getPayment().getStatus()
                : PaymentStatus.PENDING;

        return OrderSummaryDto.builder()
                .orderId(orderId)
                .serviceName(serviceName)
                .subServiceName(subServiceName)
                .items(itemSummaries)
                .itemsTotal(bill.getItemsTotalPrice())
                .gstAmount(bill.getGstAmount())
                .deliveryCharge(bill.getDeliveryCharge())
                .discountAmount(bill.getDiscountAmount())
                .finalAmount(bill.getFinalPrice())
                .isPromotionApplied(order.getPromotion() != null)
                .promotionMessage(order.getPromotion() != null ? "Promotion applied." : "No promotion.")
                .appliedPromoCode(order.getPromotion() != null ? order.getPromotion().getPromoCode() : null)
                .status(bill.getStatus())
                .build();
    }
}