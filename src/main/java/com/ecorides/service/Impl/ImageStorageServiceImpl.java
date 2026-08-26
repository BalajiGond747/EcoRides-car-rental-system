package com.ecorides.service.Impl;

import com.ecorides.exception.BadRequestException;
import com.ecorides.service.ImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final Path carUploadPath;

    public ImageStorageServiceImpl() {
        try {
            this.carUploadPath = Paths.get("uploads/cars")
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(this.carUploadPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not create car image directory", e);
        }
    }

    @Override
    public String storeCarImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Car image is required");
        }

        String contentType = file.getContentType();

        if (!List.of("image/jpeg", "image/png", "image/webp")
                .contains(contentType)) {

            throw new BadRequestException("Only JPG, PNG and WebP images are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("Car image must be smaller than 5 MB");
        }

        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + extension.toLowerCase();

        Path target = carUploadPath.resolve(fileName);

        try {

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store car image", e);
        }

        return "/images/cars/" + fileName;
    }

    @Override
    public void deleteCarImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {

            String fileName = Paths.get(imageUrl)
                    .getFileName()
                    .toString();

            Path file = carUploadPath.resolve(fileName);

            Files.deleteIfExists(file);

        } catch (Exception e) {

        }
    }
}