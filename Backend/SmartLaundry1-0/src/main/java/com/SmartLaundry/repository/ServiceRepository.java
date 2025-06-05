package com.SmartLaundry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.Services;
import java.util.*;

public interface ServiceRepository extends JpaRepository<Services, String> {
    List<Services> findAllByOrderByServiceIdAsc();
    Optional<Services> findByServiceName(String serviceName);
}
