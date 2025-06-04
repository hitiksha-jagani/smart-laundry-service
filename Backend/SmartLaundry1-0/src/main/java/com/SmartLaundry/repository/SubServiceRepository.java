package com.SmartLaundry.repository;

import com.SmartLaundry.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.SubService;
import java.util.*;

public interface SubServiceRepository extends JpaRepository<SubService, String> {
    Optional<SubService> findBySubServiceNameAndServices(String subServiceName, Services services);
    List<SubService> findAllByOrderBySubServiceIdAsc(); //SUBSV001
}
