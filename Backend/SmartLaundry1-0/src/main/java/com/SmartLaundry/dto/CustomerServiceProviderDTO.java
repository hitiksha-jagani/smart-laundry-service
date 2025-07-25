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
 private String photoImage;
 private AddressDTO address; // Use structured address DTO
 private Double averageRating;
 private List<ReviewDTO> reviews;
 private List<PriceDTO> prices;
 @JsonIgnore // ignore during JSON serialization/deserialization
 private String userName;
}
