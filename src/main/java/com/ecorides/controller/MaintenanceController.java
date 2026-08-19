package com.ecorides.controller;

import com.ecorides.payload.dto.MaintenanceDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceDTO>> createMaintenance(@Valid @RequestBody MaintenanceDTO dto) {

        MaintenanceDTO maintenance = maintenanceService.createMaintenance(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MaintenanceDTO>builder()
                        .success(true)
                        .message("Maintenance created successfully")
                        .data(maintenance)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> completeMaintenance(@PathVariable Long id) {

        MaintenanceDTO maintenance = maintenanceService.completeMaintenance(id);

        return ResponseEntity.ok(ApiResponse.<MaintenanceDTO>builder()
                .success(true)
                .message("Maintenance completed successfully")
                .data(maintenance)
                .timestamp(LocalDateTime.now())
                .build());
    }

}