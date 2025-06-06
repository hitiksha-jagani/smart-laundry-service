package com.SmartLaundry.repository;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, String> {
    List<DeliveryAgent> findAllByOrderByDeliveryAgentIdAsc(); //DA0001

    boolean existsByUsers(Users user);
}
