package com.SmartLaundry.controller.Admin;

import com.SmartLaundry.model.DeliveryAgent;
import com.SmartLaundry.model.ServiceProvider;
import com.SmartLaundry.model.Users;
import com.SmartLaundry.repository.DeliveryAgentRepository;
import com.SmartLaundry.repository.ServiceProviderRepository;
import com.SmartLaundry.repository.UserRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

@RestController
@RequestMapping("/image")
public class RedisImageController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    // http://localhost:8080/image/agent/{type}/{userId}
    @GetMapping("/agent/{type}/{userId}")
    public ResponseEntity<byte[]> fetchAgentImageFromRedis(
            @PathVariable String type,
            @PathVariable String userId) throws IOException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        DeliveryAgent agent = deliveryAgentRepository.findByUsers(user).orElse(null);

        String imageData;
        switch (type.toLowerCase()) {
            case "profile": imageData = agent.getProfilePhoto(); break;
            case "aadhar": imageData = agent.getAadharCardPhoto(); break;
            case "pan": imageData = agent.getPanCardPhoto(); break;
            case "license": imageData = agent.getDrivingLicensePhoto(); break;
            default: return ResponseEntity.badRequest().build();
        }

        File imageFile = new File(imageData);
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imagePath  = Files.readAllBytes(imageFile.toPath());

        // Detect file type
        String mimeType = Files.probeContentType(imageFile.toPath());
        MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imagePath);
    }

    // http://localhost:8080/image/provider/{type}/{userId}
    @GetMapping("/provider/{type}/{userId}")
    public ResponseEntity<byte[]> fetchProviderImageFromRedis(
            @PathVariable String type,
            @PathVariable String userId) throws IOException {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        ServiceProvider serviceProvider = serviceProviderRepository.findByUser(user);

        String imageData;
        switch (type.toLowerCase()) {
            case "profile": imageData = serviceProvider.getPhotoImage(); break;
            case "aadhar": imageData = serviceProvider.getAadharCardImage(); break;
            case "pan": imageData = serviceProvider.getPanCardImage(); break;
            case "utilitybill": imageData = serviceProvider.getBusinessUtilityBillImage(); break;
            default: return ResponseEntity.badRequest().build();
        }

        File imageFile = new File(imageData);
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] imagePath  = Files.readAllBytes(imageFile.toPath());

        // Detect file type
        String mimeType = Files.probeContentType(imageFile.toPath());
        MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imagePath);
    }

}

