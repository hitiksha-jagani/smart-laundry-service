package com.SmartLaundry.service.Customer;

import com.SmartLaundry.dto.Customer.OrderSummaryDto;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderSummaryService orderSummaryService; // reuse existing logic

    public OrderSummaryDto markBillAsPaid(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Bill bill = billRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Bill not found for order"));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalStateException("Bill is already marked as PAID");
        }

        // 1. Update bill status
        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);

        // 2. Update order status
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // 3. Save to order status history
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.COMPLETED)
                .changedAt(LocalDateTime.now())
                .build();
        orderStatusHistoryRepository.save(history);

        // 4. Return full summary with updated status
        OrderSummaryDto summary = orderSummaryService.generateOrderSummary(orderId, null);
        summary.setOrderStatus(OrderStatus.COMPLETED); // add this field in DTO
        return summary;
    }

    public OrderSummaryDto getSummaryByInvoice(String invoiceNumber) {
        Bill bill = billRepository.findById(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        Order order = bill.getOrder();
        OrderSummaryDto summary = orderSummaryService.generateOrderSummary(order.getOrderId(), null);
        summary.setOrderStatus(order.getStatus()); // Include latest status
        return summary;
    }

}
