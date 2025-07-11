package com.SmartLaundry.controller;

import com.SmartLaundry.dto.StateDTO;
import com.SmartLaundry.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/states")
@RequiredArgsConstructor
public class StateController {

    private final StateRepository stateRepository;

    @GetMapping(produces = "application/json")
    public List<StateDTO> getAllStates() {
        return stateRepository.findAll()
                .stream()
                .map(StateDTO::new)
                .toList();
    }
}
