package com.ecorides.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String storeCarImage(MultipartFile file);

    void deleteCarImage(String imageUrl);
}