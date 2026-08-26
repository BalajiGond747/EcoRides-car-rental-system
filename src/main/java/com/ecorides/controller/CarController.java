package com.ecorides.controller;

import com.ecorides.domain.CarStatus;
import com.ecorides.payload.dto.CarDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> createCar(@Valid @RequestPart("car") CarDTO dto, @RequestPart("image") MultipartFile image) {

        CarDTO car = carService.createCar(dto, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CarDTO>builder()
                        .success(true)
                        .message("Car created successfully")
                        .data(car)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> updateCar(@PathVariable Long id, @Valid @RequestPart("car") CarDTO dto, @RequestPart(value = "image", required = false) MultipartFile image) {

        CarDTO car = carService.updateCar(id, dto, image);

        return ResponseEntity.ok(ApiResponse.<CarDTO>builder()
                .success(true)
                .message("Car updated successfully")
                .data(car)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> activateCar(@PathVariable Long id) {

        carService.activateCar(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Car activated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deactivateCar(@PathVariable Long id) {

        carService.deactivateCar(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Car deactivated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> updateStatus(@PathVariable Long id, @RequestParam CarStatus status) {

        CarDTO car = carService.updateCarStatus(id, status);

        return ResponseEntity.ok(ApiResponse.<CarDTO>builder()
                .success(true)
                .message("Car status updated")
                .data(car)
                .timestamp(LocalDateTime.now())
                .build());

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CarDTO>> getCarById(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<CarDTO>builder()
                .success(true)
                .message("Car fetched successfully")
                .data(carService.getCarById(id))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<CarDTO>>> getAllCars(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size, @RequestParam(required = false) String search, @RequestParam(required = false) String category, @RequestParam(required = false) CarStatus status, @RequestParam(required = false) Boolean isActive, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<CarDTO> cars = carService.getAllCars(page, size, search, category, status, isActive, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.<PageResponse<CarDTO>>builder()
                .success(true)
                .message("Cars fetched successfully")
                .data(cars)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getAvailableCars() {

        return ResponseEntity.ok(ApiResponse.<List<CarDTO>>builder()
                .success(true)
                .message("Available cars fetched")
                .data(carService.getAvailableCars())
                .timestamp(LocalDateTime.now())
                .build());

    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByLocation(@PathVariable Long locationId) {

        return ResponseEntity.ok(ApiResponse.<List<CarDTO>>builder()
                .success(true)
                .message("Cars fetched successfully")
                .data(carService.getCarsByLocation(locationId))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByCategory(@PathVariable String category) {

        return ResponseEntity.ok(ApiResponse.<List<CarDTO>>builder()
                .success(true)
                .message("Cars fetched successfully")
                .data(carService.getCarsByCategory(category))
                .timestamp(LocalDateTime.now())
                .build());
    }

}