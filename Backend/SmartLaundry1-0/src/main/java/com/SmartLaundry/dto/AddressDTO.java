package com.SmartLaundry.dto;

import com.SmartLaundry.model.UserAddress;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
public class AddressDTO {
    private String name;
    private String areaName;
    private CityDTO city;
    private String pincode;
    private Double latitude;
    private Double longitude;

    //Constructor
    public AddressDTO(String name, String areaName, CityDTO city, String pincode, Double latitude, Double longitude) {
        this.name = name;
        this.areaName = areaName;
        this.city = city;
        this.pincode = pincode;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public AddressDTO(UserAddress userAddress) {
    }
}
