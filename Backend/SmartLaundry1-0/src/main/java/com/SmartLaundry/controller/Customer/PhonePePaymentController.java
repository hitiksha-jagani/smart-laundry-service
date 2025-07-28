package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.model.*;
import com.SmartLaundry.repository.*;
import com.SmartLaundry.service.Admin.PayoutAssignmentService;
import com.SmartLaundry.util.PhonePeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PhonePePaymentController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PayoutAssignmentService payoutAssignmentService;

    private final String MERCHANT_ID = "PGTESTPAYUAT";
    private final String SALT_KEY = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399";
    private final String SALT_INDEX = "1";

    // Temporary mapping: txnId → orderId (in-memory for test mode)
    private final Map<String, String> txnOrderMap = new HashMap<>();

    @PostMapping("/phonepe/initiate")
    public ResponseEntity<?> initiatePayment(@RequestParam String orderId) throws Exception {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Bill bill = order.getBill();

        String transactionId = "TXN_" + UUID.randomUUID();
        txnOrderMap.put(transactionId, orderId);

        String redirectUrl = "http://localhost:3000/orders/" + orderId + "/bill";

        Map<String, Object> requestBody = Map.of(
                "merchantId", MERCHANT_ID,
                "merchantTransactionId", transactionId,
                "merchantUserId", order.getUsers().getEmail(),
                "amount", bill.getFinalPrice().intValue() * 100,
                "redirectUrl", redirectUrl,
                "redirectMode", "POST",
                "paymentInstrument", Map.of("type", "PAY_PAGE")
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(requestBody);
        String base64Payload = Base64.getEncoder().encodeToString(json.getBytes());

        String xVerify = PhonePeUtil.generateXVerify(base64Payload, "/pg/v1/pay", SALT_KEY, SALT_INDEX);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("X-VERIFY", xVerify);
        headers.set("X-MERCHANT-ID", MERCHANT_ID);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("request", base64Payload), headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay",
                request,
                String.class
        );

        // ✅ Parse response JSON and extract redirectUrl
        JsonNode responseJson = mapper.readTree(response.getBody());
        String redirectUrlFromPhonePe = responseJson
                .path("data")
                .path("instrumentResponse")
                .path("redirectUrl")
                .asText();

        if (redirectUrlFromPhonePe == null || redirectUrlFromPhonePe.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get redirectUrl from PhonePe response"));
        }

        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrlFromPhonePe));
    }


    @PostMapping("/phonepe/callback")
    public ResponseEntity<?> handleCallback(@RequestParam("transactionId") String txnId) throws Exception {
        String statusUrl = "https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/status/" + MERCHANT_ID + "/" + txnId;
        String xVerify = PhonePeUtil.generateXVerify("", "/pg/v1/status/" + MERCHANT_ID + "/" + txnId, SALT_KEY, SALT_INDEX);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-VERIFY", xVerify);
        headers.set("X-MERCHANT-ID", MERCHANT_ID);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(statusUrl, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());
        String status = json.path("data").path("paymentInstrument").path("status").asText();

        if ("SUCCESS".equalsIgnoreCase(status)) {
            String orderId = txnOrderMap.get(txnId);
            if (orderId == null) return ResponseEntity.status(404).body("Order not found for txnId");

            Order order = orderRepository.findById(orderId).orElseThrow();
            Bill bill = order.getBill();

            Payment payment = Payment.builder()
                    .bill(bill)
                    .transactionId(txnId)
                    .method("PHONEPE")
                    .status(PaymentStatus.PAID)
                    .dateTime(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);
            payoutAssignmentService.addPayouts(payment);

            return ResponseEntity.ok("Payment Successful!");
        }

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed or pending.");
    }
}
