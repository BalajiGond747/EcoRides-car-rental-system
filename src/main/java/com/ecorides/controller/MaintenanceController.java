package com.ecorides.controller;

import com.ecorides.payload.dto.MaintenanceDto;
import com.ecorides.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public MaintenanceDto create(@RequestBody MaintenanceDto dto) {
        return maintenanceService.createMaintenance(dto);
    }

    @PatchMapping("/{id}/complete")
    public MaintenanceDto complete(@PathVariable Long id) {
        return maintenanceService.completeMaintenance(id);
    }
}