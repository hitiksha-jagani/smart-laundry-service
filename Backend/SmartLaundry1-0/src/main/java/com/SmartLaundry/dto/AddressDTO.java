package com.SmartLaundry.dto;

import com.SmartLaundry.model.UserAddress;
import lombok.Data;

@Data
public class AddressDTO {
    private String name;
    private String areaName;
    private CityDTO city;
    private String pincode;

    //Constructor
    public AddressDTO(UserAddress address) {
        this.name = address.getName();
        this.areaName = address.getAreaName();
        this.city = new CityDTO(address.getCity());
        this.pincode =  address.getPincode();
    }

}
