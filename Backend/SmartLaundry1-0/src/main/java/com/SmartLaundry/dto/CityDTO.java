package com.SmartLaundry.dto;

import com.SmartLaundry.model.City;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Getter
public class CityDTO {
    private String name;
    private StateDTO state;

    //Constructor

    public CityDTO(City city) {
        this.name = city.getCityName();
        this.state = new StateDTO(city.getState());
    }


}
