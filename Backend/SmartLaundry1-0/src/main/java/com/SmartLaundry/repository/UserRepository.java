package com.SmartLaundry.repository;

import com.SmartLaundry.model.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import com.SmartLaundry.model.Users;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<Users, String>{

    Optional<Users> findByEmail(String email);
    Optional<Users> findByPhoneNo(String phone);

    List<Users> findAllByOrderByUserIdAsc(); //US00001
}