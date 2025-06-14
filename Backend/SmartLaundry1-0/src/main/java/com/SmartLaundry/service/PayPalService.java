//package com.SmartLaundry.service;
//import com.SmartLaundry.model.Bill;
//import com.SmartLaundry.model.PaymentStatus;
//import com.SmartLaundry.model.Payments;
//import com.SmartLaundry.repository.BillRepository;
//import com.SmartLaundry.repository.PaymentRepository;
//import com.paypal.api.payments.*;
//import com.paypal.base.rest.APIContext;
//import com.paypal.base.rest.PayPalRESTException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class PayPalService {
//
//    @Autowired
//    private APIContext apiContext;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private BillRepository billRepository;
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
//    public Payment createPayment(Double total, String currency, String method,
//                                 String intent, String description, String cancelUrl, String successUrl,
//                                 String invoiceNumber) throws PayPalRESTException {
//
//        Amount amount = new Amount();
//        amount.setCurrency(currency);
//        amount.setTotal(String.format("%.2f", total));
//
//        Transaction transaction = new Transaction();
//        transaction.setDescription(description);
//        transaction.setAmount(amount);
//        transaction.setInvoiceNumber(invoiceNumber); // important!
//
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(transaction);
//
//        Payer payer = new Payer();
//        payer.setPaymentMethod(method.toUpperCase());
//
//        Payment payment = new Payment();
//        payment.setIntent(intent);
//        payment.setPayer(payer);
//        payment.setTransactions(transactions);
//
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setCancelUrl(cancelUrl);
//        redirectUrls.setReturnUrl(successUrl);
//        payment.setRedirectUrls(redirectUrls);
//
//        return payment.create(apiContext);
//    }
//
//
//    public Payment execute(String paymentId, String payerId) throws PayPalRESTException {
//        Payment payment = new Payment();
//        payment.setId(paymentId);
//        PaymentExecution paymentExecution = new PaymentExecution();
//        paymentExecution.setPayerId(payerId);
//        return payment.execute(apiContext, paymentExecution);
//    }
//}
