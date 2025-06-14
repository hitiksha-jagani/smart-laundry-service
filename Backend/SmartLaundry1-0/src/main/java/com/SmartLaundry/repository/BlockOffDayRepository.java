package com.SmartLaundry.repository;

import com.SmartLaundry.model.BlockOffDay;
import com.SmartLaundry.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BlockOffDayRepository extends JpaRepository<BlockOffDay, Long> {
    List<BlockOffDay> findByServiceProvider(ServiceProvider provider);
    void deleteByServiceProviderAndDate(ServiceProvider provider, LocalDate date);
}

