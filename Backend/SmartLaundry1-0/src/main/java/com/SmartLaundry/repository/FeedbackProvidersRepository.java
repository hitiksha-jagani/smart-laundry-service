package com.SmartLaundry.repository;


import com.SmartLaundry.model.FeedbackProviders;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackProvidersRepository extends JpaRepository<FeedbackProviders, Long> {
    List<FeedbackProviders> findByServiceProvider_ServiceProviderId(String ServiceProviderId);
    List<FeedbackProviders> findByServiceProvider_ServiceProviderIdOrderByCreatedAtDesc(String providerId);
    List<FeedbackProviders> findByOrder(Order order);
    List<FeedbackProviders> findByUserAndOrder(Users user, Order order);
    @Query("SELECT AVG(f.rating) FROM FeedbackProviders f WHERE f.serviceProvider.serviceProviderId = :providerId")
    Double findAverageRatingByServiceProvider(@Param("providerId") String providerId);

}

