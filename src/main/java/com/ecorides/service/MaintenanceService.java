package com.ecorides.service;

import com.ecorides.payload.dto.MaintenanceDto;

public interface MaintenanceService {

    MaintenanceDto createMaintenance(MaintenanceDto dto);

    MaintenanceDto completeMaintenance(Long id);
}