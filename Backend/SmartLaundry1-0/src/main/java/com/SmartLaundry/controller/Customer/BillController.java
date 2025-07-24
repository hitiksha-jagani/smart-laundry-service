package com.SmartLaundry.controller.Customer;

import com.SmartLaundry.dto.Customer.OrderSummaryDto;
import com.SmartLaundry.service.Customer.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping("/{orderId}/bill")
    public OrderSummaryDto getBillSummary(@PathVariable String orderId) {
        return billService.getBillSummary(orderId);
    }
}

