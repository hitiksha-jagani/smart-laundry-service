package com.SmartLaundry.dto;

import com.SmartLaundry.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDTO {
    private Long stateId;
    private String name;

    //Constructor

    public StateDTO(State state) {
        this.name = state.getStateName();
    }
}
