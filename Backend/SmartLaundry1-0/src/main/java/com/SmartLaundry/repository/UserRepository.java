package com.SmartLaundry.repository;

import com.SmartLaundry.model.UserPrincipal;
import com.SmartLaundry.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.Users;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<Users, String> , JpaSpecificationExecutor<Users> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByPhoneNo(String phone);

    List<Users> findAllByOrderByUserIdAsc(); //US00001

    long countByRoleAndCreatedAtBetween(UserRole role, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT ua.city.cityName, COUNT(u.userId)
        FROM Users u
        JOIN UserAddress ua ON u.userId = ua.users.userId
        WHERE u.role = :role
        GROUP BY ua.city.cityName
    """)
    List<Object[]> countCustomersGroupedByCity(UserRole role);

    boolean existsByRole(UserRole admin);
}