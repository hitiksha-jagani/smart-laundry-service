package com.SmartLaundry.repository;

import com.SmartLaundry.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.SubService;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface SubServiceRepository extends JpaRepository<SubService, String> {
    Optional<SubService> findBySubServiceNameAndServices(String subServiceName, Services services);
    List<SubService> findAllByOrderBySubServiceIdAsc(); //SUBSV001
    List<SubService> findByServices_ServiceId(String serviceId);

    @Query("SELECT s.subServiceName FROM SubService s")
    List<String> findAllSubServiceNames();

    List<SubService> findByServices(Services service);

    long countByServices(Services service);
}
