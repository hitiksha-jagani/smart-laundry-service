package com.SmartLaundry.repository;

import com.SmartLaundry.model.GeocodingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GeocodingConfigRepository extends JpaRepository<GeocodingConfig, Long> {

    GeocodingConfig findTopByOrderByCreatedAtDesc();

    List<GeocodingConfig> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT g.apiProvider FROM GeocodingConfig g")
    List<String> findAllApiProvider();

    GeocodingConfig findByActiveStatus(boolean b);
}
