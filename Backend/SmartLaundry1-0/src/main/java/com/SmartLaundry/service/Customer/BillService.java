package com.SmartLaundry.service.Customer;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BillService {

    private final OrderRepository orderRepository;
    private final BillRepository billRepository;

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
