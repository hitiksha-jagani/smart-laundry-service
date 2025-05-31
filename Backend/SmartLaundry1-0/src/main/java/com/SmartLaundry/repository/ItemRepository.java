package com.SmartLaundry.repository;

import com.SmartLaundry.model.Items;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Items, String> {
    List<Items> findAllByOrderByItemIdAsc(); //IT001
}
