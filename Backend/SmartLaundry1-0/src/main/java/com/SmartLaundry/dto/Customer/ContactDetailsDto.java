package com.SmartLaundry.dto.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailsDto {
    private String contactName;
    private String contactPhone;
    private String contactAddress;
    private Double latitude;
    private Double longitude;
}
