package com.SmartLaundry.repository;

import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, String> {
    boolean existsByUser(Users user);
}
