package com.SmartLaundry.dto;

import com.SmartLaundry.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StateDTO {
    private String name;

    //Constructor

    public StateDTO() {}

    public StateDTO(State state) {
        this.name = state.getStateName();
    }
}
