package com.SmartLaundry.controller.DeliveryAgent;

import com.SmartLaundry.model.Users;
import com.SmartLaundry.service.Admin.RoleCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-detail")
public class FetchUserController {

    @Autowired
    private RoleCheckingService roleCheckingService;

    @GetMapping("/{userId}")
    public ResponseEntity<Users> getUserDetail(@PathVariable String userId){
        Users user = roleCheckingService.checkUser(userId);
        System.out.println("id : " + user.getUserId());
        System.out.println("email : " + user.getEmail());
        return ResponseEntity.ok(user);
    }

}
