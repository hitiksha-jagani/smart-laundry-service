package com.SmartLaundry.controller;

import com.SmartLaundry.dto.CityDTO;
import com.SmartLaundry.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository cityRepository;

    @GetMapping(produces = "application/json")
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .map(CityDTO::new)
                .toList();
    }
}

