package com.SmartLaundry.service.Image;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private final String rootDir = "images/";

    @Override
    public String saveImage(MultipartFile file, String folder, String userId) throws IOException {
        String filename = userId + "_" + file.getOriginalFilename();
        Path dirPath = Paths.get(rootDir, folder);
        Files.createDirectories(dirPath);
        Path filePath = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    @Override
    public Resource loadImage(String path) throws IOException {
        Path filePath = Paths.get(path);
        return new UrlResource(filePath.toUri());
    }

    @Override
    public void deleteImage(String path) throws IOException {
        Files.deleteIfExists(Paths.get(path));
    }
}

