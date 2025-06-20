package com.SmartLaundry.repository;

import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findByUsers(Users users);
}
