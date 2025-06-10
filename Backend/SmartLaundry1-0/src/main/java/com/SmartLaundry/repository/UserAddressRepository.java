package com.SmartLaundry.repository;

import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    UserAddress findByUsers(Users user);
}
