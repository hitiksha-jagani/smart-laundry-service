package com.SmartLaundry.repository;


import com.SmartLaundry.model.FeedbackProviders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackProvidersRepository extends JpaRepository<FeedbackProviders, Long> {
    List<FeedbackProviders> findByServiceProvider_ServiceProviderId(String ServiceProviderId);
}

