package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.ManageServiceListingRequestDTO;
import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Services;
import com.SmartLaundry.model.SubService;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.ItemRepository;
import com.SmartLaundry.repository.ServiceRepository;
import com.SmartLaundry.repository.SubServiceRepository;
import com.SmartLaundry.repository.UserRepository;
import jakarta.mail.FetchProfile;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

// @author Hitiksha Jagani
@Service
public class ServiceListingService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubServiceRepository subServiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    // Add item data
    public String addItemDetails(String userId, ManageServiceListingRequestDTO manageServiceListingRequestDTO) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        Services service = serviceRepository.findByServiceName(manageServiceListingRequestDTO.getServiceName())
                .orElseThrow(() -> new RuntimeException("Service is not available."));

        SubService subService = subServiceRepository.findBySubServiceNameAndServices(manageServiceListingRequestDTO.getSubServiceName(), service)
                .orElseThrow(() -> new RuntimeException("Sub service is not available."));

        Items item = Items.builder()
                .itemName(manageServiceListingRequestDTO.getItemName())
                .service(service)
                .subService(subService)
                .build();

        itemRepository.save(item);

        return "Item added successfully.";
    }

    // Add service data
    public String addServiceDetails(String userId, ManageServiceListingRequestDTO manageServiceListingRequestDTO) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        Services service = Services.builder()
                .serviceName(manageServiceListingRequestDTO.getServiceName())
                .build();

        serviceRepository.save(service);

        return "Service added successfully";
    }

    // Add sub-service data
    public String addSubServiceDetails(String userId, ManageServiceListingRequestDTO manageServiceListingRequestDTO) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!"ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("You are not applicable for this page.");
        }

        Services service = serviceRepository.findByServiceName(manageServiceListingRequestDTO.getServiceName())
                .orElseThrow(() -> new RuntimeException("Service is not available."));

        SubService subService = SubService.builder()
                .services(service)
                .subServiceName(manageServiceListingRequestDTO.getSubServiceName())
                .build();

        subServiceRepository.save(subService);

        return "Sub-service added successfully";
    }
}
