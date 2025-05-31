package com.SmartLaundry.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
public class DeliveryAgentHomeController {

    // http://localhost:8080/deliveries/summary
    // Return numbers of total deliveries, pending requests, today's deliveries
    @GetMapping("/summary")
    public Integer getDeliveries(){

        return 3;
    }



}
