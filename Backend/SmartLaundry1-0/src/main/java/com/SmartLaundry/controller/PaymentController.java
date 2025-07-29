//package com.SmartLaundry.controller;
//
//import com.SmartLaundry.model.Bill;
//import com.SmartLaundry.service.Customer.BillService;
//import com.SmartLaundry.service.PayPalService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PayPalService paypalService;
//    private final BillService billService;
//
//    /**
//     * Create PayPal order for a given bill
//     * @param billId ID of the bill
//     * @param mobile true if initiated from mobile app
//     * @return PayPal order ID
//     */
//    @PostMapping("/create")
//    public ResponseEntity<?> createPayment(@RequestParam String billId,
//                                           @RequestParam(defaultValue = "false") boolean mobile) {
//        Bill bill = billService.findById(billId);
//        if (bill == null || bill.getFinalPrice() <= 0) {
//            return ResponseEntity.badRequest().body("Invalid bill or amount.");
//        }
//
//        String orderId = paypalService.createPayment(bill, bill.getFinalPrice(), mobile);
//        return ResponseEntity.ok(Map.of("orderID", orderId));
//    }
//
//    /**
//     * Called after PayPal payment approval to capture and record it
//     * @param orderId PayPal order ID
//     * @param billId  Bill to be marked as paid
//     * @return success message
//     */
//    @GetMapping("/success")
//    public ResponseEntity<String> success(@RequestParam("orderId") String orderId,
//                                          @RequestParam("billId") String billId) {
//        Bill bill = billService.findById(billId);
//        if (bill == null) {
//            return ResponseEntity.badRequest().body("Bill not found.");
//        }
//
//        paypalService.captureAndStorePayment(orderId, bill);
//        return ResponseEntity.ok("Payment successful and recorded.");
//    }
//}
