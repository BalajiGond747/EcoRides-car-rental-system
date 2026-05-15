package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.Maintenance;
import com.ecorides.payload.dto.MaintenanceDto;
import com.ecorides.repository.CarRepository;
import com.ecorides.repository.MaintenanceRepository;
import com.ecorides.service.CarStatusLogService;
import com.ecorides.service.MaintenanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final CarRepository carRepository;
    private final CarStatusLogService carStatusLogService;

    @Override
    @Transactional
    public MaintenanceDto createMaintenance(MaintenanceDto dto) {

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        Maintenance maintenance = Maintenance.builder()
                .car(car)
                .type(dto.getType())
                .description(dto.getDescription())
                .status(MaintenanceStatus.IN_PROGRESS)
                .startDate(LocalDate.now())
                .build();

        car.setStatus(CarStatus.MAINTENANCE);

        carStatusLogService.logStatus(car, CarStatus.MAINTENANCE);

        maintenanceRepository.save(maintenance);

        return dto;
    }

    @Override
    @Transactional
    public MaintenanceDto completeMaintenance(Long id) {

        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));

        maintenance.setStatus(MaintenanceStatus.COMPLETED);
        maintenance.setEndDate(LocalDate.now());

        Car car = maintenance.getCar();
        car.setStatus(CarStatus.AVAILABLE);

        carStatusLogService.logStatus(car, CarStatus.AVAILABLE);

        maintenanceRepository.save(maintenance);

        return MaintenanceDto.builder()
                .id(maintenance.getId())
                .carId(car.getId())
                .status("COMPLETED")
                .build();
    }
}