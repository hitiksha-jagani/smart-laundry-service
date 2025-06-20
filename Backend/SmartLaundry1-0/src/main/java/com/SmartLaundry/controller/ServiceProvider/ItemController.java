package com.SmartLaundry.controller.ServiceProvider;
import com.SmartLaundry.model.Items;
import com.SmartLaundry.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // For CORS
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    // GET /item/all
    @GetMapping("/all")
    public List<Items> getAllItems() {
        return itemRepository.findAll();
    }
}

