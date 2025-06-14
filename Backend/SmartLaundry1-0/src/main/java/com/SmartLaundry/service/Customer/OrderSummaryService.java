//package com.SmartLaundry.service.Customer;
//
//import com.SmartLaundry.dto.Customer.OrderSummaryDto;
//import com.SmartLaundry.model.*;
//import com.SmartLaundry.repository.BillRepository;
//import com.SmartLaundry.repository.OrderRepository;
//import com.SmartLaundry.repository.PriceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class OrderSummaryService {
//
//    private final OrderRepository orderRepository;
//    private final BillRepository billRepository;
//    private final PromotionEvaluatorService promotionEvaluatorService;
//    private final PriceRepository priceRepository;
//
//    public OrderSummaryDto generateOrderSummary(String orderId, Promotion promo) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        List<BookingItem> bookingItems = order.getBookingItems();
//
//        double itemsTotal = bookingItems.stream()
//                .mapToDouble(item -> item.getFinalPrice() != null ? item.getFinalPrice() : 0.0)
//                .sum();
//
//        double gstAmount = itemsTotal * 0.18;
//        double deliveryCharge = 30.0;
//
//        double discount = 0.0;
//        String promoMessage = "";
//        boolean isValidPromotion = false;
//
//        if (promo != null) {
//            BigDecimal totalBeforeDiscount = BigDecimal.valueOf(itemsTotal + gstAmount + deliveryCharge);
//            String validationMessage = promotionEvaluatorService.getPromotionValidationMessage(
//                    promo, bookingItems, totalBeforeDiscount, order.getCreatedAt()
//            );
//
//            if (validationMessage == null) {
//                isValidPromotion = true;
//                discount = promotionEvaluatorService
//                        .calculateDiscount(promo, totalBeforeDiscount)
//                        .doubleValue();
//                promoMessage = "Promotion applied.";
//            } else {
//                promoMessage = validationMessage;
//            }
//        }
//
//        double finalAmount = itemsTotal + gstAmount + deliveryCharge - discount;
//
//        // Save bill if it doesn't exist
//        Optional<Bill> existingBill = billRepository.findByOrder(order);
//        if (existingBill.isEmpty()) {
//            Bill bill = Bill.builder()
//                    .order(order)
//                    .status(BillStatus.PENDING_FOR_PAYMENT)
//                    .itemsTotalPrice(itemsTotal)
//                    .gstAmount(gstAmount)
//                    .deliveryCharge(deliveryCharge)
//                    .discountAmount(discount)
//                    .finalPrice(finalAmount)
//                    .build();
//
//            List<BookingItem> billItems = bookingItems.stream().map(item -> {
//                item.setBill(bill);
//                return item;
//            }).toList();
//
//            bill.setBookingItems(billItems);
//
//            billRepository.save(bill);
//        }
//
//        // Build item summary list
//        List<OrderSummaryDto.ItemSummary> itemSummaries = bookingItems.stream().map(item -> {
//            Items itemEntity = item.getItem();
//            Long pricePerUnit = priceRepository
//                    .findByServiceProviderAndItem(order.getServiceProvider(), itemEntity)
//                    .map(Price::getPrice)
//                    .orElse(0L);
//
//            return OrderSummaryDto.ItemSummary.builder()
//                    .itemName(itemEntity.getItemName())
//                    .quantity(item.getQuantity())
//                    .price(pricePerUnit.doubleValue())
//                    .finalPrice(item.getFinalPrice())
//                    .build();
//        }).toList();
//
//        // Handle null SubService/Service safely
//        String subServiceName = "";
//        String serviceName = "";
//        if (!bookingItems.isEmpty()) {
//            Items firstItem = bookingItems.get(0).getItem();
//            if (firstItem != null && firstItem.getSubService() != null) {
//                subServiceName = Optional.ofNullable(firstItem.getSubService().getSubServiceName()).orElse("");
//                if (firstItem.getSubService().getServices() != null) {
//                    serviceName = Optional.ofNullable(firstItem.getSubService().getServices().getServiceName()).orElse("");
//                }
//            }
//        }
//
//        return OrderSummaryDto.builder()
//                .orderId(orderId)
//                .serviceName(serviceName)
//                .subServiceName(subServiceName)
//                .items(itemSummaries)
//                .itemsTotal(itemsTotal)
//                .gstAmount(gstAmount)
//                .deliveryCharge(deliveryCharge)
//                .discountAmount(discount)
//                .finalAmount(finalAmount)
//                .isPromotionApplied(isValidPromotion)
//                .promotionMessage(promoMessage)
//                .orderStatus(order.getStatus())
//                .build();
//    }
//}
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

        // If no promo passed, use the one from the order
        if (promo == null) {
            promo = order.getPromotion();
        }

        List<BookingItem> bookingItems = order.getBookingItems();

        double itemsTotal = bookingItems.stream()
                .mapToDouble(item -> item.getFinalPrice() != null ? item.getFinalPrice() : 0.0)
                .sum();

        double gstAmount = itemsTotal * 0.18;
        double deliveryCharge = 30.0;

        double discount = 0.0;
        String promoMessage = "";
        boolean isValidPromotion = false;

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

        double finalAmount = itemsTotal + gstAmount + deliveryCharge - discount;

        // Save bill if it doesn't exist
        Optional<Bill> existingBill = billRepository.findByOrder(order);
        if (existingBill.isEmpty()) {
            Bill bill = Bill.builder()
                    .order(order)
                    .status(BillStatus.PENDING_FOR_PAYMENT)
                    .itemsTotalPrice(itemsTotal)
                    .gstAmount(gstAmount)
                    .deliveryCharge(deliveryCharge)
                    .discountAmount(discount)
                    .finalPrice(finalAmount)
                    .build();

            List<BookingItem> billItems = bookingItems.stream().map(item -> {
                item.setBill(bill);
                return item;
            }).toList();

            bill.setBookingItems(billItems);
            billRepository.save(bill);
        }

        // Build item summary list
        List<OrderSummaryDto.ItemSummary> itemSummaries = bookingItems.stream().map(item -> {
            Items itemEntity = item.getItem();
            Long pricePerUnit = priceRepository
                    .findByServiceProviderAndItem(order.getServiceProvider(), itemEntity)
                    .map(Price::getPrice)
                    .orElse(0L);

            return OrderSummaryDto.ItemSummary.builder()
                    .itemName(itemEntity.getItemName())
                    .quantity(item.getQuantity())
                    .price(pricePerUnit.doubleValue())
                    .finalPrice(item.getFinalPrice())
                    .build();
        }).toList();

        // Handle null SubService/Service safely
        String subServiceName = "";
        String serviceName = "";

        if (!bookingItems.isEmpty()) {
            Items firstItem = bookingItems.get(0).getItem();
            if (firstItem != null) {
                SubService subService = firstItem.getSubService();
                if (subService != null) {
                    subServiceName = Optional.ofNullable(subService.getSubServiceName()).orElse("");

                    Services service = subService.getServices();
                    if (service != null) {
                        serviceName = Optional.ofNullable(service.getServiceName()).orElse("");
                    }
                }
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
                .discountAmount(discount)
                .finalAmount(finalAmount)
                .isPromotionApplied(isValidPromotion)
                .promotionMessage(promoMessage)
                .appliedPromoCode(promo != null ? promo.getPromoCode() : null) // ✅ Add this line
                .orderStatus(order.getStatus())
                .build();
    }
}
