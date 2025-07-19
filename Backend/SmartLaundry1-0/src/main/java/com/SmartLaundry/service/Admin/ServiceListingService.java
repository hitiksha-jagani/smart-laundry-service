package com.SmartLaundry.service.Admin;

import com.SmartLaundry.dto.Admin.ManageServiceListingRequestDTO;
import com.SmartLaundry.dto.Admin.ServiceSummaryDTO;
import com.SmartLaundry.model.Items;
import com.SmartLaundry.model.Services;
import com.SmartLaundry.model.SubService;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.ItemRepository;
import com.SmartLaundry.repository.ServiceRepository;
import com.SmartLaundry.repository.SubServiceRepository;
import com.SmartLaundry.repository.UserRepository;
import jakarta.mail.FetchProfile;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private RoleCheckingService roleCheckingService;

    // Get summary
    public List<ServiceSummaryDTO> getServiceSummary() {
        List<Services> services = serviceRepository.findAll();

        return services.stream()
                .map(service -> {
                    long subServiceCount = subServiceRepository.countByServices(service);
                    return new ServiceSummaryDTO(
                            service.getServiceId(),
                            service.getServiceName(),
                            subServiceCount
                    );
                })
                .collect(Collectors.toList());
    }


    // Add item data
    public String addItemDetails(String userId, ManageServiceListingRequestDTO manageServiceListingRequestDTO) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        roleCheckingService.isAdmin(user);

        Services service = serviceRepository.findByServiceName(manageServiceListingRequestDTO.getServiceName())
                .orElseThrow(() -> new RuntimeException("Service is not available."));

        SubService subService = null;
        String subServiceName = manageServiceListingRequestDTO.getSubServiceName();

        if (StringUtils.hasText(subServiceName)) {
            subService = subServiceRepository.findBySubServiceNameAndServices(subServiceName, service)
                    .orElseThrow(() -> new RuntimeException("Sub-service is not available."));
        }

        Items item = Items.builder()
                .itemName(manageServiceListingRequestDTO.getItemName())
                .service(service)
                .subService(subService)
                .build();

        try {
            itemRepository.save(item);
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(v -> {
                System.out.println("Validation failed: " + v.getPropertyPath() + " - " + v.getMessage());
            });
            throw e;
        }

        return "Item added successfully.";
    }

    // Add service data
    public String addServiceDetails(String userId, ManageServiceListingRequestDTO manageServiceListingRequestDTO) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        roleCheckingService.isAdmin(user);

        Services service = Services.builder()
                .serviceName(manageServiceListingRequestDTO.getServiceName())
                .build();

        serviceRepository.save(service);

        return "Service added successfully";
    }

    // Fetch all services
    public List<String> getServices(String userId) {
        return serviceRepository.findAllServiceNames();
    }

    // Add sub-service data
    public String addSubServiceDetails(String userId, ManageServiceListingRequestDTO manageServiceListingRequestDTO) throws AccessDeniedException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        roleCheckingService.isAdmin(user);

//        Services service = serviceRepository.findByServiceName(manageServiceListingRequestDTO.getServiceName())
//                .orElseThrow(() -> new RuntimeException("Service is not available."));

        Services detachedService = serviceRepository.findByServiceName(manageServiceListingRequestDTO.getServiceName())
                .orElseThrow(() -> new RuntimeException("Service is not available."));

        // Ensure managed
        Services managedService = serviceRepository.findById(detachedService.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found by ID."));

        SubService subService = SubService.builder()
                .services(managedService)
                .subServiceName(manageServiceListingRequestDTO.getSubServiceName())
                .build();

        subServiceRepository.save(subService);

        return "Sub-service added successfully";
    }

    public List<String> getSubServices(String userId) {
        return subServiceRepository.findAllSubServiceNames();
    }

    public List<String> getSubServiceNamesByServiceName(String serviceName) {
        Services service = serviceRepository.findByServiceName(serviceName)
                .orElseThrow(() -> new RuntimeException("Service not found: " + serviceName));

        List<SubService> subServices = subServiceRepository.findByServices(service);
        return subServices.stream()
                .map(SubService::getSubServiceName)
                .collect(Collectors.toList());
    }

}
