package com.SmartLaundry.repository;

import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Price;
import com.SmartLaundry.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByServiceProvider(ServiceProvider serviceProvider);
    Optional<Price> findByServiceProviderAndItem(ServiceProvider serviceProvider, Items item);
    void deleteByServiceProvider(ServiceProvider serviceProvider);
}
