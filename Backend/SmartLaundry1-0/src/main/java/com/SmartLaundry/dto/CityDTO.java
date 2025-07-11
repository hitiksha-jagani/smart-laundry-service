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
    private Long cityId;
    private String name;
    private StateDTO state;

    //Constructor

    public CityDTO(City city) {
        this.cityId = city.getCityId();
        this.name = city.getCityName();
        this.state = new StateDTO(city.getState());
    }


}
