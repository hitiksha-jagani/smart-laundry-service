package com.SmartLaundry.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.mail.FetchProfile;
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
  //  @JsonIgnore
   private String serviceProviderId;
    private String businessName;
    private byte[] photoImage; // Base64 string
    private AddressDTO address; // Use structured address DTO
    private Long averageRating;
    private List<ReviewDTO> reviews;
    private List<ItemDTO> items;
    private String userName;
}

