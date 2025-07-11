package com.SmartLaundry.repository;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.Status;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, String> {
    Optional<DeliveryAgent> findByUsers_UserId(String userId);

    List<DeliveryAgent> findAllByOrderByDeliveryAgentIdAsc(); //DA0001
  //  Optional<DeliveryAgent> findByAgentId(String deliveryAgentId);
    boolean existsByUsers(Users user);

    Optional<DeliveryAgent> findByUsers(Users user);

    List<DeliveryAgent> findByStatus(Status status);
    Optional<DeliveryAgent> findByDeliveryAgentId(String deliveryAgentId);

    DeliveryAgent findByUsersUserId(String id);

    boolean existsByUsers_UserId(String userId);

    @Modifying
    @Query("UPDATE DeliveryAgent da SET da.currentLatitude = :lat, da.currentLongitude = :lon WHERE da.users.userId = :userId")
    void updateLocation(@Param("userId") String userId, @Param("lat") Double lat, @Param("lon") Double lon);

}
