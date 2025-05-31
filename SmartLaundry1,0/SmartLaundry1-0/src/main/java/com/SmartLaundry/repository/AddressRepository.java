package com.SmartLaundry.repository;

import com.SmartLaundry.model.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<UserAddress, Long> {
}
