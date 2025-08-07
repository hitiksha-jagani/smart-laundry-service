package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.OrderSummaryDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.OrderRepository;
import com.SmartLaundry.repository.PriceRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public OrderSummaryDto generateOrderSummary(String orderId, Promotion promo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (promo == null) {
            promo = order.getPromotion(); // fallback to already applied promotion
        }

        List<BookingItem> bookingItems = order.getBookingItems();

        double itemsTotal = bookingItems.stream()
                .mapToDouble(item -> item.getFinalPrice() != null ? item.getFinalPrice() : 0.0)
                .sum();

        double gstAmount = itemsTotal * 0.18;

        Bill bill = billRepository.findByOrder(order);
        if (bill == null || bill.getDeliveryCharge() == null) {
            throw new IllegalStateException("Delivery charge is not calculated yet. Please try again later.");
        }

        double deliveryCharge = bill.getDeliveryCharge();
        double discount = bill.getDiscountAmount() != null ? bill.getDiscountAmount() : 0.0;
        boolean updated = false;
        String promoMessage = "";

        // ✅ Validate and apply promotion
        if (promo != null) {
            BigDecimal totalBeforeDiscount = BigDecimal.valueOf(itemsTotal + gstAmount + deliveryCharge);

            String validationMessage = promotionEvaluatorService.getPromotionValidationMessage(
                    promo, bookingItems, totalBeforeDiscount, order.getCreatedAt()
            );

            if (validationMessage == null) {
                promoMessage = "Promotion applied.";

                boolean isNewPromo = order.getPromotion() == null ||
                        !order.getPromotion().getPromotionId().equals(promo.getPromotionId());

                if (isNewPromo) {
                    order.setPromotion(promo);
                    orderRepository.save(order);
                }

                // ✅ Always recalculate discount
                discount = promotionEvaluatorService.calculateDiscount(promo, totalBeforeDiscount).doubleValue();
                bill.setDiscountAmount(discount);
                bill.setFinalPrice(itemsTotal + gstAmount + deliveryCharge - discount);
                updated = true;

            } else {
                promoMessage = validationMessage;
            }
        }

        // ✅ Ensure totals are saved
        if (bill.getItemsTotalPrice() == null || bill.getItemsTotalPrice() == 0.0) {
            bill.setItemsTotalPrice(itemsTotal);
            updated = true;
        }

        if (bill.getGstAmount() == null || bill.getGstAmount() == 0.0) {
            bill.setGstAmount(gstAmount);
            updated = true;
        }

        if (bill.getFinalPrice() == null || bill.getFinalPrice() == 0.0) {
            bill.setFinalPrice(itemsTotal + gstAmount + deliveryCharge - discount);
            updated = true;
        }

        if (updated) {
            billRepository.save(bill);
        }

        // ✅ Build item summary
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

        // ✅ Service/Sub-service names
        String serviceName = "";
        String subServiceName = "";
        if (!bookingItems.isEmpty()) {
            Items firstItem = Optional.ofNullable(bookingItems.get(0)).map(BookingItem::getItem).orElse(null);
            if (firstItem != null) {
                SubService sub = firstItem.getSubService();
                Services service = sub != null ? sub.getServices() : firstItem.getService();

                subServiceName = sub != null ? sub.getSubServiceName() : "N/A";
                serviceName = service != null ? service.getServiceName() : "N/A";
            }
        }

        // ✅ Return DTO with orderStatus included
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
                .promotionMessage(promoMessage)
                .appliedPromoCode(order.getPromotion() != null ? order.getPromotion().getPromoCode() : null)
                .status(bill.getStatus()) // BillStatus (e.g., PAID, UNPAID)
                .orderStatus(order.getStatus()) // ✅ New field (OrderStatus)
                .invoiceNumber(bill.getInvoiceNumber())
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

    @Transactional
    public void markBillAsPaid(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Bill bill = billRepository.findByOrder(order);
        if (bill == null) {
            throw new RuntimeException("No bill found for order: " + orderId);
        }
       
        Payment payment = bill.getPayment();
        if (payment == null || payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Payment is not completed yet for order: " + orderId);
        }

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);
    }
}
