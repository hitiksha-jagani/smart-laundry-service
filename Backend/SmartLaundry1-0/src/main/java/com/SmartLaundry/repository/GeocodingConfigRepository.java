package com.SmartLaundry.repository;

import com.SmartLaundry.model.GeocodingConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeocodingConfigRepository extends JpaRepository<GeocodingConfig, Long> {

    GeocodingConfig findTopByOrderByCreatedAtDesc();

    List<GeocodingConfig> findAllByOrderByCreatedAtDesc();
}
