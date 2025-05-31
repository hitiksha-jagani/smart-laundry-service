package com.SmartLaundry.repository;

import com.SmartLaundry.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
