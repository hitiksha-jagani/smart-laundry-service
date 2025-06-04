package com.SmartLaundry.repository;


import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Services;
import com.SmartLaundry.model.SubService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemsRepository extends JpaRepository<Items, String> {
    Optional<Items> findByItemNameAndServiceAndSubService(String itemName, Services service, SubService subService);
}
