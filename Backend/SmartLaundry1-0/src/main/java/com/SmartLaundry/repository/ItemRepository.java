package com.SmartLaundry.repository;

import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Services;
import com.SmartLaundry.model.SubService;
import jakarta.mail.FetchProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Items, String> {
    List<Items> findAllByOrderByItemIdAsc(); //IT001


    Optional<Items> findByItemNameAndServiceAndSubService(String itemName, Services service, SubService subService);

    Optional<Items> findByItemName(String itemName);
}
