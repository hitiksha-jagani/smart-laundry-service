package com.SmartLaundry.service.Admin;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.UserAddress;
import com.SmartLaundry.model.UserRole;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.UserAddressRepository;
import com.SmartLaundry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public class RoleCheckingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    // Check user exist or not and if exist then return user
    public Users checkUser(String userId){

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not exist."));

        return user;
    }

    // Check user address exist or not and if exist then return user address
    public UserAddress checkUserAddress(Users user){
        UserAddress address = user.getAddress();
        if (address == null) {
            System.out.println("No address found for user: " + user.getUserId());
        } else {
            System.out.println("Address found: " + address);
        }
        return address;
    }

    // Check delivery agent exist or not and if exist then return delivery agent
    public DeliveryAgent checkDeliveryAgent(Users user){

        DeliveryAgent deliveryAgent = deliveryAgentRepository.findByUsers(user)
                .orElseThrow(() -> new UsernameNotFoundException("Delivery agent not exist."));

        return deliveryAgent;
    }

    // Check whether user is admin or not
    public void isAdmin(Users user) throws AccessDeniedException {
        if (!UserRole.ADMIN.equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }
    }

    // Check whether user is delivery agent or not
    public void isDeliveryAgent(Users user) throws AccessDeniedException {
        if (!UserRole.DELIVERY_AGENT.equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }
    }

}
