package com.SmartLaundry.controller;

import com.SmartLaundry.dto.CityDTO;
import com.SmartLaundry.model.City;
import com.SmartLaundry.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get/{stateName}")
    public List<CityDTO> getCitiesByState(@PathVariable String stateName) {
        List<City> cities = cityRepository.findByState_StateName(stateName);
        return cities.stream().map(CityDTO::new).toList();
    }


}

