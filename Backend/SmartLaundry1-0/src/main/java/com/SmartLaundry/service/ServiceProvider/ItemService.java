package com.SmartLaundry.service.ServiceProvider;
import com.SmartLaundry.model.Items;
import com.SmartLaundry.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<Items> getByServiceAndSubService(String serviceId, String subServiceId) {
        return itemRepository.findByService_ServiceIdAndSubService_SubServiceId(serviceId, subServiceId);
    }
}

