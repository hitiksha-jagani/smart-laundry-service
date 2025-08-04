package com.SmartLaundry.config;

import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdminUser() {
        boolean adminExists = userRepository.existsByRole(UserRole.ADMIN);

        if (!adminExists) {
            Users admin = new Users();
            admin.setFirstName("Dummy");
            admin.setLastName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPhoneNo("9999999999");
            admin.setRole(UserRole.ADMIN);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setBlocked(false);

            userRepository.save(admin);

            System.out.println("✅ Default admin created: admin@example.com / admin123");
        } else {
            System.out.println("ℹ️ Admin already exists. Skipping creation.");
        }
    }

}
