package com.SmartLaundry.dto;

import com.SmartLaundry.model.City;
import lombok.Data;


@Data
public class CityDTO {
    private String name;
    private StateDTO state;

    //Constructor

    public CityDTO() {}

    public CityDTO(City city) {
        this.name = city.getCityName();
        this.state = new StateDTO(city.getState());
    }
}
