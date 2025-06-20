package com.SmartLaundry.dto.ServiceProvider;

import com.SmartLaundry.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveOrderGroupedDto {
    private String orderId;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private OrderStatus status;
    private List<ActiveOrderDto> items;
}
