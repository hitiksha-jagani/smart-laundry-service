package com.SmartLaundry.repository;

import com.SmartLaundry.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByCityName(String cityName);

    List<City> findByState_StateId(Long stateId);

    List<City> findByState_StateName(String stateName);
}
