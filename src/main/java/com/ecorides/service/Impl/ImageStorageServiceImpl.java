package com.ecorides.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecorides.exception.BadRequestException;
import com.ecorides.service.ImageStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageServiceImpl.class);

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    private static final String CAR_IMAGE_FOLDER = "ecorides/cars";

    private final Cloudinary cloudinary;
    private final String cloudName;

    public ImageStorageServiceImpl(Cloudinary cloudinary, @Value("${cloudinary.cloud-name}") String cloudName) {

        this.cloudinary = cloudinary;
        this.cloudName = cloudName;
    }

    @Override
    public String storeCarImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Car image is required");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only JPG, PNG and WebP images are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Car image must be smaller than 5 MB");
        }

        try {

            String publicId = UUID.randomUUID()
                    .toString();

            Map<?, ?> uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap("folder", CAR_IMAGE_FOLDER, "public_id", publicId, "resource_type", "image", "overwrite", false));

            String secureUrl = (String) uploadResult.get("secure_url");

            if (secureUrl == null || secureUrl.isBlank()) {
                throw new RuntimeException("Cloudinary did not return an image URL");
            }

            log.info("Car image uploaded successfully to Cloudinary");

            return secureUrl;

        } catch (Exception e) {

            log.error("Failed to upload car image to Cloudinary", e);

            throw new RuntimeException("Failed to upload car image. Please try again.", e);
        }
    }

    @Override
    public void deleteCarImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {

            String publicId = extractPublicId(imageUrl);

            if (publicId == null) {
                return;
            }

            cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.asMap("resource_type", "image", "invalidate", true));

            log.info("Car image deleted from Cloudinary: {}", publicId);

        } catch (Exception e) {

            log.warn("Could not delete old Cloudinary image: {}", imageUrl, e);
        }
    }

    private String extractPublicId(String imageUrl) {

        try {

            URI uri = URI.create(imageUrl);

            if (!"res.cloudinary.com".equalsIgnoreCase(uri.getHost())) {
                return null;
            }

            String[] parts = uri.getPath()
                    .split("/");

            if (parts.length < 6) {
                return null;
            }

            if (!cloudName.equals(parts[1])) {
                return null;
            }

            if (!"image".equals(parts[2]) || !"upload".equals(parts[3])) {
                return null;
            }

            int startIndex = 4;

            if (startIndex < parts.length && parts[startIndex].matches("v\\d+")) {
                startIndex++;
            }

            if (startIndex >= parts.length) {
                return null;
            }

            StringBuilder publicId = new StringBuilder();

            for (int i = startIndex; i < parts.length; i++) {

                if (parts[i].isBlank()) {
                    continue;
                }

                if (!publicId.isEmpty()) {
                    publicId.append("/");
                }

                publicId.append(parts[i]);
            }

            String result = publicId.toString();

            int lastSlash = result.lastIndexOf('/');
            int lastDot = result.lastIndexOf('.');

            if (lastDot > lastSlash) {
                result = result.substring(0, lastDot);
            }

            return result.isBlank() ? null : result;

        } catch (Exception e) {

            log.warn("Unable to extract Cloudinary public ID", e);

            return null;
        }
    }
}
