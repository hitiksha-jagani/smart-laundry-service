package com.SmartLaundry.repository;
import com.SmartLaundry.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    // You can add custom queries if needed, but basic CRUD is covered by JpaRepository
}

