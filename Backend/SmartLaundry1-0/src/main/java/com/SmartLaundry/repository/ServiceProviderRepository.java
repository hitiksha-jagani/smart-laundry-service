package com.SmartLaundry.repository;

import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, String> {
    List<ServiceProvider> findByUser_UserIdIn(Set<String> userIds);
    boolean existsByUser(Users user);

    Optional<ServiceProvider> getByUser(Users user);
    ServiceProvider findByUser(Users user);
    @Query("SELECT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.user u LEFT JOIN FETCH u.address WHERE sp.serviceProviderId = :id")
    Optional<ServiceProvider> findByIdWithUserAddress(@Param("id") String serviceProviderId);

    @Query("SELECT DISTINCT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.user u LEFT JOIN FETCH u.address")
    List<ServiceProvider> findAllWithUserAddresses();
    Optional<ServiceProvider> findByUserUserId(String userId);

}
