package com.SmartLaundry.repository;


import com.SmartLaundry.model.FeedbackProviders;
import com.SmartLaundry.model.Order;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackProvidersRepository extends JpaRepository<FeedbackProviders, Long> {
    List<FeedbackProviders> findByServiceProvider_ServiceProviderId(String ServiceProviderId);
    List<FeedbackProviders> findByServiceProvider_ServiceProviderIdOrderByCreatedAtDesc(String providerId);
    List<FeedbackProviders> findByOrder(Order order);
    List<FeedbackProviders> findByUserAndOrder(Users user, Order order);

}

