package com.SmartLaundry.controller.ServiceProvider;

import com.SmartLaundry.dto.ItemDTO;
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
    public List<ItemDTO> getAllItems() {
        List<Items> items = itemRepository.findAllWithServiceAndSubService();
        return items.stream().map(ItemDTO::new).toList();
    }
}
