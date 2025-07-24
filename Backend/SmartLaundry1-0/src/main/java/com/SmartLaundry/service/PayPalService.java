//package com.SmartLaundry.service;
//
//import com.paypal.orders.Order;
//import com.SmartLaundry.config.PayPalClient;
//import com.SmartLaundry.model.*;
//import com.SmartLaundry.repository.BillRepository;
//import com.SmartLaundry.repository.PaymentRepository;
//import com.paypal.http.HttpResponse;
//import com.paypal.orders.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PayPalService {
//
//    private final PayPalClient payPalClient;
//    private final BillRepository billRepository;
//    private final PaymentRepository paymentRepository;
//
//    private static final double USD_TO_INR = 83.00;
//
//    public String createPayment(Bill bill, double totalAmountInINR) {
//        double amountUSD = totalAmountInINR / USD_TO_INR;  // Convert to USD
//
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.checkoutPaymentIntent("CAPTURE");
//
//        orderRequest.applicationContext(new ApplicationContext()
//                .returnUrl("http://localhost:3000/payment/success?billId=" + bill.getInvoiceNumber())
//                .cancelUrl("http://localhost:3000/payment/cancel")
//                .brandName("SmartLaundry")
//                .landingPage("LOGIN")
//                .userAction("PAY_NOW")
//                .shippingPreference("NO_SHIPPING"));
//
//        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
//                .referenceId(bill.getInvoiceNumber())
//                .description("SmartLaundry Payment")
//                .amountWithBreakdown(new AmountWithBreakdown()
//                        .currencyCode("USD")
//                        .value(String.format("%.2f", amountUSD)));  // Pass USD to PayPal
//
//        orderRequest.purchaseUnits(List.of(purchaseUnit));
//        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
//
//        try {
//            HttpResponse<Order> response = payPalClient.client().execute(request);
//            return response.result().id();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to create PayPal order", e);
//        }
//    }
//
//    public void captureAndStorePayment(String orderId, Bill bill) {
//        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
//        request.requestBody(new OrderRequest());
//
//        try {
//            HttpResponse<Order> response = payPalClient.client().execute(request);
//            Order order = response.result();
//
//            Capture capture = order.purchaseUnits().get(0).payments().captures().get(0);
//            String transactionId = capture.id();
//
//            // Save Payment
//            Payment payment = Payment.builder()
//                    .transactionId(transactionId)
//                    .status(PaymentStatus.PAID)
//                    .method("PayPal")
//                    .dateTime(LocalDateTime.now())
//                    .bill(bill)
//                    .build();
//            paymentRepository.save(payment);
//
//            // Update Bill
//            bill.setPayment(payment);
//            bill.setStatus(BillStatus.PAID);
//            billRepository.save(bill);
//
//        } catch (IOException e) {
//            throw new RuntimeException("PayPal capture failed", e);
//        }
//    }
//}
//
package com.SmartLaundry.service;

import com.paypal.orders.Order;
import com.SmartLaundry.config.PayPalClient;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.PaymentRepository;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayPalService {

    private final PayPalClient payPalClient;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;

    private static final double USD_TO_INR = 83.00;

    public String createPayment(Bill bill, double totalAmountInINR) {
        double amountUSD = totalAmountInINR / USD_TO_INR;  // Convert to USD

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        orderRequest.applicationContext(new ApplicationContext()
                .returnUrl("http://localhost:3000/payment/success?billId=" + bill.getInvoiceNumber())
                .cancelUrl("http://localhost:3000/payment/cancel")
                .brandName("SmartLaundry")
                .landingPage("LOGIN")
                .userAction("PAY_NOW")
                .shippingPreference("NO_SHIPPING"));

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .referenceId(bill.getInvoiceNumber())
                .description("SmartLaundry Payment")
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(String.format("%.2f", amountUSD)));  // Pass USD to PayPal

        orderRequest.purchaseUnits(List.of(purchaseUnit));
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> response = payPalClient.client().execute(request);
            return response.result().id();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create PayPal order", e);
        }
    }

    public void captureAndStorePayment(String orderId, Bill bill) {
        // 1. Check if payment already exists for this bill
        if (paymentRepository.existsByBillInvoiceNumberAndStatus(bill.getInvoiceNumber(), PaymentStatus.PAID)) {
            // Avoid double capture attempt
            return;
        }

        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());

        try {
            HttpResponse<Order> response = payPalClient.client().execute(request);
            Order order = response.result();

            if (order.purchaseUnits() == null || order.purchaseUnits().isEmpty()
                    || order.purchaseUnits().get(0).payments() == null
                    || order.purchaseUnits().get(0).payments().captures() == null
                    || order.purchaseUnits().get(0).payments().captures().isEmpty()) {
                throw new RuntimeException("Invalid PayPal capture response");
            }

            Capture capture = order.purchaseUnits().get(0).payments().captures().get(0);
            String transactionId = capture.id();

            Payment payment = Payment.builder()
                    .transactionId(transactionId)
                    .status(PaymentStatus.PAID)
                    .method("PayPal")
                    .dateTime(LocalDateTime.now())
                    .bill(bill)
                    .build();
            paymentRepository.save(payment);

            bill.setPayment(payment);
            bill.setStatus(BillStatus.PAID);
            billRepository.save(bill);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("PayPal capture failed", e);
        }
    }

}