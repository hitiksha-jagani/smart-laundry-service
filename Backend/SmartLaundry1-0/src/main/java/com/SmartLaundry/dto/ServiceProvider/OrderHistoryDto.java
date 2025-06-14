package com.SmartLaundry.dto.ServiceProvider;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class OrderHistoryDto {
    private String orderId;
    private String status;
    private List<ItemDetail> items;

    @Data
    @Builder
    public static class ItemDetail {
        private String serviceName;
        private String subServiceName;
        private String itemName;
        private int quantity;
    }
}
