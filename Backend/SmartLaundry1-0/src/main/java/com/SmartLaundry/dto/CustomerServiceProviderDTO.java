package com.SmartLaundry.dto;

import com.SmartLaundry.dto.Admin.PriceDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerServiceProviderDTO {
 private String serviceProviderId;
 private String businessName;
 private byte[] photoImage; // Base64 string
 private AddressDTO address; // Use structured address DTO
 private Long averageRating;
 private List<ReviewDTO> reviews;
 private List<PriceDTO> prices;
 private List<ItemDTO> items;
 @JsonIgnore // ignore during JSON serialization/deserialization
 private String userName;
}
