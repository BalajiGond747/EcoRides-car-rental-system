package com.ecorides.service;

import com.ecorides.payload.dto.MaintenanceDTO;

public interface MaintenanceService {

    MaintenanceDTO createMaintenance(MaintenanceDTO dto);

    MaintenanceDTO completeMaintenance(Long id);
}