package com.SmartLaundry.service;

import com.SmartLaundry.config.PayPalClient;
import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.BillRepository;
import com.SmartLaundry.repository.PaymentRepository;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.paypal.orders.Order;
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

    public String createPayment(Bill bill, double totalAmountInINR, boolean mobile) {
        double amountUSD = totalAmountInINR / USD_TO_INR;

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        String returnUrl = mobile
                ? "smartlaundry://payment/success?billId=" + bill.getInvoiceNumber() + "&orderId=" + bill.getOrder().getOrderId()
                : "http://localhost:3000/payment/success?billId=" + bill.getInvoiceNumber() + "&orderId=" + bill.getOrder().getOrderId();

        String cancelUrl = mobile
                ? "smartlaundry://payment/cancel"
                : "http://localhost:3000/payment/cancel";

        orderRequest.applicationContext(new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .brandName("SmartLaundry")
                .landingPage("LOGIN")
                .userAction("PAY_NOW")
                .shippingPreference("NO_SHIPPING"));

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .referenceId(bill.getInvoiceNumber())
                .description("SmartLaundry Payment")
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(String.format("%.2f", amountUSD)));

        orderRequest.purchaseUnits(List.of(purchaseUnit));
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> response = payPalClient.client().execute(request);
            return response.result().links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Approval link not found"))
                    .href();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create PayPal order", e);
        }
    }



    public void captureAndStorePayment(String orderId, Bill bill) {
        if (paymentRepository.existsByBillInvoiceNumberAndStatus(bill.getInvoiceNumber(), PaymentStatus.PAID)) {
            return; // Already paid
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
            throw new RuntimeException("PayPal capture failed", e);
        }
    }
}
