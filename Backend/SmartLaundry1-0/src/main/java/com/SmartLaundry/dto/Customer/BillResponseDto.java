package com.SmartLaundry.dto.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDto {
    private String invoiceNumber;
    private Double itemsTotalPrice;
    private Double deliveryCharge;
    private Double gstAmount;
    private Double discountAmount;
    @JsonProperty("Total") // This changes the JSON key from "finalPrice" to "total"
    private Double finalPrice;
    private String status;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private List<BookingItemDto> items;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class BookingItemDto {
        private String itemName;
        private String service;
        private String subService;
        private Integer quantity;
        private Double price;
        private Double finalPrice;
    }
}

