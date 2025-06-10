package com.SmartLaundry.dto.ServiceProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderHistoryDto {
    private String orderId;
    private String serviceName;
    private String subServiceName;
    private String itemName;
    private int quantity;
    private String status;
}

