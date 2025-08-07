package com.SmartLaundry.service.Image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    String saveImage(MultipartFile file, String folder, String userId) throws IOException;

    Resource loadImage(String path) throws IOException;

    void deleteImage(String path) throws IOException;
}

