package com.SmartLaundry.repository;

import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Status;
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
    @Query("SELECT sp FROM ServiceProvider sp JOIN FETCH sp.user u JOIN FETCH u.address")
    List<ServiceProvider> findAllWithUserAddresses();

    Optional<ServiceProvider> findByServiceProviderId(String serviceProviderId);
    Optional<ServiceProvider> getByUser(Users user);
    ServiceProvider findByUser(Users user);
//    @Query("SELECT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.user u LEFT JOIN FETCH u.address WHERE sp.serviceProviderId = :id")
//    Optional<ServiceProvider> findByIdWithUserAddress(@Param("id") String serviceProviderId);
@Query("SELECT sp FROM ServiceProvider sp " +
        "JOIN FETCH sp.user u " +
        "JOIN FETCH u.address a " +
        "JOIN FETCH a.city " +
        "WHERE sp.serviceProviderId = :id")
Optional<ServiceProvider> findByIdWithUserAddress(@Param("id") String id);

//    @Query("SELECT DISTINCT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.user u LEFT JOIN FETCH u.address")
//    List<ServiceProvider> findAllWithUserAddresses();
    Optional<ServiceProvider> findByUserUserId(String userId);

    List<ServiceProvider> findByBusinessNameContainingIgnoreCase(String keyword);
    @Query("SELECT DISTINCT sp FROM ServiceProvider sp " +
            "LEFT JOIN FETCH sp.prices pr " +
            "JOIN FETCH sp.user u " +
            "LEFT JOIN FETCH u.address a")
    List<ServiceProvider> findAllWithPricesAndUserAddresses();
//    @Query("""
//    SELECT sp FROM ServiceProvider sp
//    LEFT JOIN FETCH sp.prices p
//    LEFT JOIN FETCH p.item i
//    JOIN FETCH sp.user u
//    LEFT JOIN FETCH u.address a
//    WHERE sp.serviceProviderId = :id
//""")
//    Optional<ServiceProvider> findByIdWithPricesAndUserAddress(@Param("id") String id);
@Query("""
    SELECT sp FROM ServiceProvider sp
    LEFT JOIN FETCH sp.prices p
    LEFT JOIN FETCH p.item i
    LEFT JOIN FETCH i.service
    LEFT JOIN FETCH i.subService
    JOIN FETCH sp.user u
    LEFT JOIN FETCH u.address a
    WHERE sp.serviceProviderId = :id
""")
Optional<ServiceProvider> findByIdWithPricesAndUserAddress(@Param("id") String id);
    Optional<ServiceProvider> findByUser_UserId(String userId);

    Optional<ServiceProvider> findById(String id);


    @Query("SELECT sp FROM ServiceProvider sp LEFT JOIN FETCH sp.prices WHERE sp.serviceProviderId = :id")
    Optional<ServiceProvider> findByIdWithPrices(@Param("id") String id);

    List<ServiceProvider> findByStatus(Status status);
    @Query("""
    SELECT sp FROM ServiceProvider sp
    LEFT JOIN FETCH sp.schedulePlans
    WHERE sp.serviceProviderId = :id
""")
    Optional<ServiceProvider> findByIdWithSchedulePlans(@Param("id") String id);


}
