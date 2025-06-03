package com.SmartLaundry.controller;

import com.SmartLaundry.Publisher.RedisMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publish")
public class PublishController {

    @Autowired
    private RedisMessagePublisher publisher;

    @GetMapping("/{message}")
    public String publish(@PathVariable String message) {
        publisher.publish("my-channel", message);
        return "Message published: " + message;
    }
}
