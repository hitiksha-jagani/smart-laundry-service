package com.SmartLaundry.service;

import com.SmartLaundry.model.Bill;
import com.SmartLaundry.repository.BillRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private final BillRepository billRepository;

    public String createRazorpayOrder(Bill bill) {
        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

            JSONObject options = new JSONObject();
            options.put("amount", bill.getFinalPrice() * 100);
            options.put("currency", "INR");
            options.put("receipt", bill.getInvoiceNumber());
            options.put("payment_capture", 1);

            Order order = razorpay.orders.create(options);
            return order.get("id");
        } catch (Exception e) {
            throw new RuntimeException("Razorpay order creation failed", e);
        }
    }
}

