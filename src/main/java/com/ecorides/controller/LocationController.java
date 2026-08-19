package com.ecorides.controller;

import com.ecorides.payload.dto.LocationDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationDTO>> createLocation(@Valid @RequestBody LocationDTO dto) {

        LocationDTO location = locationService.createLocation(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LocationDTO>builder()
                        .success(true)
                        .message("Location created successfully")
                        .data(location)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> getLocation(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<LocationDTO>builder()
                .success(true)
                .message("Location fetched successfully")
                .data(locationService.getLocationById(id))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAllLocations() {

        return ResponseEntity.ok(ApiResponse.<List<LocationDTO>>builder()
                .success(true)
                .message("Locations fetched successfully")
                .data(locationService.getAllLocations())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getActiveLocations() {

        return ResponseEntity.ok(ApiResponse.<List<LocationDTO>>builder()
                .success(true)
                .message("Active locations fetched")
                .data(locationService.getActiveLocations())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getByCity(@PathVariable String city) {

        return ResponseEntity.ok(ApiResponse.<List<LocationDTO>>builder()
                .success(true)
                .message("Locations fetched successfully")
                .data(locationService.getLocationsByCity(city))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationDTO dto) {

        return ResponseEntity.ok(ApiResponse.<LocationDTO>builder()
                .success(true)
                .message("Location updated successfully")
                .data(locationService.updateLocation(id, dto))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Object>> activateLocation(@PathVariable Long id) {

        locationService.activateLocation(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Location activated")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateLocation(@PathVariable Long id) {

        locationService.deactivateLocation(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Location deactivated")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

}