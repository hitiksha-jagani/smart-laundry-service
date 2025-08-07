package com.SmartLaundry.controller;

import com.SmartLaundry.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Upload a file to Cloudinary
     * @param file the image file
     * @param folder optional folder name in Cloudinary (default: "uploads")
     * @return the image URL
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "uploads") String folder) {
        try {
            String imageUrl = cloudinaryService.uploadFile(file, folder);
            return ResponseEntity.ok().body(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Image upload failed: " + e.getMessage());
        }
    }
}
