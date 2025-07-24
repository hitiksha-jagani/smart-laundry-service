package com.SmartLaundry.repository;

import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Services;
import com.SmartLaundry.model.SubService;
import jakarta.mail.FetchProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Items, String> {
    List<Items> findAllByOrderByItemIdAsc(); //IT001
    Optional<Items> findByItemNameAndServiceAndSubService(String itemName, Services service, SubService subService);
    List<Items> findByService_ServiceIdAndSubService_SubServiceId(String serviceId, String subServiceId);
    Optional<Items> findByItemName(String itemName);
    @Query("SELECT i FROM Items i LEFT JOIN FETCH i.service LEFT JOIN FETCH i.subService")
    List<Items> findAllWithServiceAndSubService();

}
