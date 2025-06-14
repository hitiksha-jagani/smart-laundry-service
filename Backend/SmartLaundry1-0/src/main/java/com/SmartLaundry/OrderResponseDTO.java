package com.SmartLaundry.dto.DeliveryAgent;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private String orderId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String providerName;
    private String providerPhone;
    private String providerAddress;
    private Long totalQuantity;

    private List<OrderResponseDTO.BookingItemDTO> bookingItemDTOList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingItemDTO{
        private String itemName;
        private String serviceName;
        private Integer quantity;
    }
}
