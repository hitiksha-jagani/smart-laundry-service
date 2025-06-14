package com.SmartLaundry.controller;
//
//import com.SmartLaundry.dto.Customer.OrderSummaryDto;
//import com.SmartLaundry.model.*;
//import com.SmartLaundry.repository.BillRepository;
//import com.SmartLaundry.repository.PaymentRepository;
//import com.SmartLaundry.service.Customer.BillService;
//import com.SmartLaundry.service.PayPalService;
//import com.paypal.api.payments.Payment;
//import com.paypal.api.payments.PaymentExecution;
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.PayPalRESTException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//
//@RestController
//@RequestMapping("/paypal")
//@CrossOrigin("http://localhost:63342")
//public class PayPalController {
//    @Autowired
//    private APIContext apiContext;
//
//    @Autowired
//    private PayPalService paypalService;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private BillRepository billRepository;
//    @Autowired
//    private BillService billService;
//
//    @Value("${paypal.client.id}")
//    private String clientId;
//
//    @Value("${paypal.client.secret}")
//    private String clientSecret;
//
//    @Value("${paypal.mode}")
//    private String mode;
//
//
//    //GET http://localhost:8080/paypal/pay?billId=IN00021
//    @GetMapping("/pay")
//    public String pay(@RequestParam("billId") String billId) {
//        try {
//            Bill bill = billRepository.findById(billId)
//                    .orElseThrow(() -> new RuntimeException("Bill not found"));
//
//            double usdAmount = bill.getFinalPrice() / 83.0;
//            String invoiceNumber = bill.getInvoiceNumber();
//
//            String approvalUrl = paypalService.createPayment(usdAmount, invoiceNumber);
//
//            // Remove "Redirect to: " prefix if present
//            if (approvalUrl.startsWith("Redirect to: ")) {
//                approvalUrl = approvalUrl.replace("Redirect to: ", "").trim();
//            }
//
//            return "redirect:" + approvalUrl;  // Important: Redirects to PayPal UI
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "redirect:/payment-error.html";  // You can create this error page
//        }
//    }
//
//
//    @GetMapping("/bills/{invoiceNumber}")
//    public ResponseEntity<OrderSummaryDto> getBillByInvoice(@PathVariable String invoiceNumber) {
//        return ResponseEntity.ok(billService.getSummaryByInvoice(invoiceNumber));
//    }
//
//    @GetMapping("/paypal/success")
//    public String successPay(@RequestParam("paymentId") String paymentId,
//                             @RequestParam("PayerID") String payerId) {
//        try {
//            System.out.println("Attempting to execute payment: " + paymentId);
//
//            Payment payment = paypalService.execute(paymentId, payerId);
//            System.out.println("Payment state: " + payment.getState());
//
//            if ("approved".equalsIgnoreCase(payment.getState())) {
//                String invoiceNumber = payment.getTransactions().get(0).getInvoiceNumber();
//                System.out.println("Payment approved for invoice: " + invoiceNumber);
//
//                Bill bill = billRepository.findById(invoiceNumber)
//                        .orElseThrow(() -> new RuntimeException("Bill not found"));
//
//                Payments newPayment = Payments.builder()
//                        .paypalPaymentId(paymentId)
//                        .amount(bill.getFinalPrice())
//                        .dateTime(LocalDateTime.now())
//                        .status(PaymentStatus.PAID)
//                        .bill(bill)
//                        .build();
//
//                paymentRepository.save(newPayment);
//                System.out.println("Payment saved successfully!");
//
//                if (bill.getStatus() != BillStatus.PAID) {
//                    bill.setStatus(BillStatus.PAID);
//                    billRepository.save(bill);
//                    System.out.println("Bill status updated to PAID");
//                }
//
//                return "redirect:/payment-success.html?invoice=" + invoiceNumber;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "redirect:/payment-failure.html";
//    }
//
//
//
//}


import com.SmartLaundry.model.Bill;
import com.SmartLaundry.model.BillStatus;
import com.SmartLaundry.model.PaymentStatus;
import com.SmartLaundry.model.Payments;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.PaymentRepository;
import com.SmartLaundry.service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/paypal")
@CrossOrigin("http://localhost:63342")
public class PayPalController {

    @Autowired
    private PayPalService paypalService;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    private static final String SUCCESS_URL  = "http://localhost:8080/paypal/success";
    private static final String CANCEL_URL   = "http://localhost:8080/paypal/cancel";

    @PostMapping("/pay")
    public ResponseEntity<?> makePayment(@RequestParam double amount, @RequestParam String userId) {
        try {
            // Step 1: Get all matching pending bills for the user and amount
            List<Bill> matchingBills = billRepository
                    .findByFinalPriceAndStatusAndUsers_UserId(amount, BillStatus.PENDING, userId);

            if (matchingBills.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No pending bills found for amount: " + amount);
            }

            if (matchingBills.size() == 1) {
                // Step 2: Only one bill → proceed to payment
                Bill bill = matchingBills.get(0);
                String invoiceNumber = bill.getInvoiceNumber();

                Payment payment = paypalService.createPayment(
                        amount,
                        "USD",
                        "paypal",
                        "sale",
                        "Payment for invoice: " + invoiceNumber,
                        CANCEL_URL,
                        SUCCESS_URL,
                        invoiceNumber
                );

                for (Links link : payment.getLinks()) {
                    if ("approval_url".equals(link.getRel())) {
                        return ResponseEntity.ok("Redirect to: " + link.getHref());
                    }
                }

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unable to find approval link in PayPal response");
            } else {
                // Step 3: Multiple bills found → let frontend choose
                List<String> invoiceNumbers = matchingBills.stream()
                        .map(Bill::getInvoiceNumber)
                        .toList();

                return ResponseEntity.status(HttpStatus.MULTIPLE_CHOICES)
                        .body(invoiceNumbers);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }




    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId) {

        try {
            Payment payment = paypalService.execute(paymentId, payerId);
            if (payment.getState().equals("approved")) {

                // Extract invoice number
                String invoiceNumber = payment.getTransactions().get(0).getInvoiceNumber();
                Double amount = Double.parseDouble(payment.getTransactions().get(0).getAmount().getTotal());

                // Fetch related bill
                Bill bill = billRepository.findById(invoiceNumber)
                        .orElseThrow(() -> new RuntimeException("Bill not found for invoice: " + invoiceNumber));

                // Save payment
                Payments dbPayment = Payments.builder()
                        .bill(bill)
                        .amount(amount)
                        .dateTime(LocalDateTime.now())
                        .status(PaymentStatus.PAID)
                        .paypalPaymentId(paymentId)
                        .build();

                paymentRepository.save(dbPayment);

                // Update bill status if needed
                bill.setStatus(BillStatus.PAID); // if applicable
                billRepository.save(bill);

                return "Payment successful for invoice: " + invoiceNumber;
            }
        } catch (PayPalRESTException e) {
            if (e.getDetails() != null && "DUPLICATE_TRANSACTION".equals(e.getDetails().getName())) {
                return "Duplicate invoice number. Please try again with a new transaction.";
            }
            throw new RuntimeException(e);
        }


        return "Payment failed";
    }


}