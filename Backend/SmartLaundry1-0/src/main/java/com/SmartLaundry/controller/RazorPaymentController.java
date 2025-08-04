package com.SmartLaundry.controller;

import com.SmartLaundry.dto.RazorpaySuccessDTO;
import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.BillStatus;
import com.SmartLaundry.model.Payment;
import com.SmartLaundry.model.PaymentStatus;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.PaymentRepository;
import com.SmartLaundry.service.Admin.PayoutAssignmentService;
import com.SmartLaundry.service.RazorpayService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class RazorPaymentController {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;
    private final PayoutAssignmentService payoutAssignmentService;

    @PostMapping("/create/{invoiceNumber}")
    public ResponseEntity<?> createOrder(@PathVariable String invoiceNumber) {
        Bill bill = billRepository.findById(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invalid invoice number"));

        String orderId = razorpayService.createRazorpayOrder(bill);
        return ResponseEntity.ok(Map.of("orderId", orderId, "amount", bill.getFinalPrice() * 100));
    }

    @PostMapping("/success")
    @Transactional
    public ResponseEntity<?> paymentSuccess(@RequestBody RazorpaySuccessDTO dto) {
        System.out.println("Received Razorpay success payload: " + dto);

        if (dto.getInvoiceNumber() == null || dto.getPaymentId() == null || dto.getMethod() == null) {
            return ResponseEntity.badRequest().body("Missing required payment data");
        }

        Bill bill = billRepository.findById(dto.getInvoiceNumber())
                .orElseThrow(() -> new RuntimeException("Bill not found: " + dto.getInvoiceNumber()));

        // Save payment
        Payment payment = Payment.builder()
                .bill(bill)
                .transactionId(dto.getPaymentId())
                .method(dto.getMethod())
                .dateTime(LocalDateTime.now())
                .status(PaymentStatus.PAID)
                .build();

        bill.setStatus(BillStatus.PAID);
        bill.setPayment(payment);

        billRepository.save(bill);

        // Trigger payouts
        payoutAssignmentService.addPayouts(payment);

        return ResponseEntity.ok("Payment recorded and payouts added");
    }


}