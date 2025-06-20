package com.SmartLaundry.controller;

import com.SmartLaundry.model.UserRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @GetMapping
    public List<String> getAllRoles() {
        return Arrays.stream(UserRole.values())
                .map(Enum::name)
                .toList();
    }
}
