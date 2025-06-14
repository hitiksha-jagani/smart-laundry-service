package com.SmartLaundry.dto.ServiceProvider;
import lombok.Data;

@Data
public class AcceptOrderRequestDto {
    private String orderId;
    private boolean needOfDeliveryAgent;
}

