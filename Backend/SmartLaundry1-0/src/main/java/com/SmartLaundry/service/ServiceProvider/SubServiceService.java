package com.SmartLaundry.service.ServiceProvider;
import com.SmartLaundry.model.SubService;
import com.SmartLaundry.repository.SubServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubServiceService {
    private final SubServiceRepository subServiceRepository;

    public List<SubService> getByServiceId(String serviceId) {
        return subServiceRepository.findByServices_ServiceId(serviceId);
    }
}

