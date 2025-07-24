package com.SmartLaundry.config;

import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupChecker {

    public static boolean adminSetupRequired = true;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(SetupChecker.class);

    @PostConstruct
    public void init() {
        boolean adminExists = userRepository.existsByRole(UserRole.ADMIN);
        adminSetupRequired = !adminExists;
        log.info("Admin exists? {}", adminExists);
        log.info("Admin setup required: {}", adminSetupRequired);
    }
}
