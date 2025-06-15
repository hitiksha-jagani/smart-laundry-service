package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.OrderSummaryDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderSummaryService {

    private final OrderRepository orderRepository;
    private final BillRepository billRepository;
    private final PromotionEvaluatorService promotionEvaluatorService;
    private final PriceRepository priceRepository;

    public OrderSummaryDto generateOrderSummary(String orderId, Promotion promo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (promo == null) {
            promo = order.getPromotion();
        }

        List<BookingItem> bookingItems = order.getBookingItems();

        double itemsTotal = bookingItems.stream()
                .mapToDouble(item -> item.getFinalPrice() != null ? item.getFinalPrice() : 0.0)
                .sum();

        double gstAmount = itemsTotal * 0.18;

        // Step 1: Try fetching existing bill
        Bill existingBill = billRepository.findByOrder(order);

        double deliveryCharge;
        double discount = 0.0;
        String promoMessage = "";
        boolean isValidPromotion = false;

        // Step 2: Require delivery charge from existing bill
        if (existingBill == null || existingBill.getDeliveryCharge() == null) {
            throw new IllegalStateException("Delivery charge is not calculated yet. Please try again later.");
        }
        deliveryCharge = existingBill.getDeliveryCharge();

        // Step 3: Validate and apply promotion (if any)
        if (promo != null) {
            BigDecimal totalBeforeDiscount = BigDecimal.valueOf(itemsTotal + gstAmount + deliveryCharge);
            String validationMessage = promotionEvaluatorService.getPromotionValidationMessage(
                    promo, bookingItems, totalBeforeDiscount, order.getCreatedAt()
            );

            if (validationMessage == null) {
                isValidPromotion = true;
                discount = promotionEvaluatorService
                        .calculateDiscount(promo, totalBeforeDiscount)
                        .doubleValue();
                promoMessage = "Promotion applied.";

                if (order.getPromotion() == null ||
                        !order.getPromotion().getPromotionId().equals(promo.getPromotionId())) {
                    order.setPromotion(promo);
                    orderRepository.save(order);
                }
            } else {
                promoMessage = validationMessage;
            }
        }

        // Step 4: Calculate final amount
        double finalAmount = itemsTotal + gstAmount + deliveryCharge - (isValidPromotion ? discount : 0.0);

        // Step 5: Create bill only if not present
        if (existingBill == null) {
            Bill bill = Bill.builder()
                    .order(order)
                    .status(BillStatus.PENDING_FOR_PAYMENT)
                    .itemsTotalPrice(itemsTotal)
                    .gstAmount(gstAmount)
                    .deliveryCharge(deliveryCharge)
                    .discountAmount(isValidPromotion ? discount : 0.0)
                    .finalPrice(finalAmount)
                    .build();

            List<BookingItem> copiedItems = copyBookingItems(bookingItems, bill, order);
            bill.setBookingItems(copiedItems);
            billRepository.save(bill);
        }

        // Step 6: Build item summaries
        List<OrderSummaryDto.ItemSummary> itemSummaries = bookingItems.stream().map(item -> {
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

        // Step 7: Extract service/subservice names
        String serviceName = "";
        String subServiceName = "";
        if (!bookingItems.isEmpty()) {
            Items firstItem = Optional.ofNullable(bookingItems.get(0)).map(BookingItem::getItem).orElse(null);
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

        return OrderSummaryDto.builder()
                .orderId(orderId)
                .serviceName(serviceName)
                .subServiceName(subServiceName)
                .items(itemSummaries)
                .itemsTotal(itemsTotal)
                .gstAmount(gstAmount)
                .deliveryCharge(deliveryCharge)
                .discountAmount(isValidPromotion ? discount : 0.0)
                .finalAmount(finalAmount)
                .isPromotionApplied(isValidPromotion)
                .promotionMessage(promoMessage)
                .appliedPromoCode(promo != null ? promo.getPromoCode() : null)
                .orderStatus(order.getStatus())
                .build();
    }

    private List<BookingItem> copyBookingItems(List<BookingItem> originalItems, Bill bill, Order order) {
        return originalItems.stream().map(original -> {
            BookingItem copy = new BookingItem();
            copy.setItem(original.getItem());
            copy.setQuantity(original.getQuantity());
            copy.setFinalPrice(original.getFinalPrice());
            copy.setOrder(order);
            copy.setBill(bill);
            return copy;
        }).toList();
    }
}