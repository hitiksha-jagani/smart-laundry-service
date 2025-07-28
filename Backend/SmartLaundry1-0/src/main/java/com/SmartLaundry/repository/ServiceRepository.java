package com.SmartLaundry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.Services;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface ServiceRepository extends JpaRepository<Services, String> {
    Optional<Services> findByServiceName(String serviceName);
    List<Services> findAllByOrderByServiceIdAsc();

    @Query("SELECT s.serviceName FROM Services s")
    List<String> findAllServiceNames();
}
