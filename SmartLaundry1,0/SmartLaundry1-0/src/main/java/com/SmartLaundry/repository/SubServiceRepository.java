package com.SmartLaundry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.SubService;
import java.util.*;

public interface SubServiceRepository extends JpaRepository<SubService, String> {
    List<SubService> findAllByOrderBySubServiceIdAsc(); //SUBSV001
}
