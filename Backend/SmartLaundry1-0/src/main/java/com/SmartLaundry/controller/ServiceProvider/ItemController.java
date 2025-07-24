package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.model.Items;
import com.SmartLaundry.repository.ItemRepository;
import com.SmartLaundry.service.ServiceProvider.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController("spItemController") // avoid bean name conflict
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    // GET /item/all
    @GetMapping("/all")
    public List<Items> getAllItems() {
        return itemRepository.findAll();
    }

//    // GET /item?serviceId=...&subServiceId=...
//    @GetMapping
//    public List<Items> getByServiceAndSubService(
//            @RequestParam String serviceId,
//            @RequestParam String subServiceId
//    ) {
//        return itemService.getByServiceAndSubService(serviceId, subServiceId);
//    }
}
