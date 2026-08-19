package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.Maintenance;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.payload.dto.MaintenanceDTO;
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
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final CarRepository carRepository;
    private final CarStatusLogService carStatusLogService;

    @Override
    public MaintenanceDTO createMaintenance(MaintenanceDTO dto) {

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + dto.getCarId()));

        if (!Boolean.TRUE.equals(car.getIsActive())) {
            throw new BadRequestException("Car is inactive");
        }

        if (car.getStatus() == CarStatus.MAINTENANCE) {
            throw new BadRequestException("Car is already under maintenance");
        }

        Maintenance maintenance = Maintenance.builder()
                .car(car)
                .type(dto.getType())
                .description(dto.getDescription())
                .status(MaintenanceStatus.IN_PROGRESS)
                .startDate(LocalDate.now())
                .build();

        car.setStatus(CarStatus.MAINTENANCE);
        carStatusLogService.logStatus(car, CarStatus.MAINTENANCE);

        Maintenance saved = maintenanceRepository.save(maintenance);

        return buildDto(saved);
    }

    @Override
    public MaintenanceDTO completeMaintenance(Long id) {

        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found with id: " + id));

        if (maintenance.getStatus() == MaintenanceStatus.COMPLETED) {
            throw new BadRequestException("Maintenance is already completed");
        }

        maintenance.setStatus(MaintenanceStatus.COMPLETED);
        maintenance.setEndDate(LocalDate.now());

        Car car = maintenance.getCar();
        car.setStatus(CarStatus.AVAILABLE);

        carStatusLogService.logStatus(car, CarStatus.AVAILABLE);

        Maintenance saved = maintenanceRepository.save(maintenance);

        return buildDto(saved);
    }

    private MaintenanceDTO buildDto(Maintenance maintenance) {

        return MaintenanceDTO.builder()
                .id(maintenance.getId())
                .carId(maintenance.getCar()
                        .getId())
                .type(maintenance.getType())
                .description(maintenance.getDescription())
                .status(maintenance.getStatus())
                .startDate(maintenance.getStartDate())
                .endDate(maintenance.getEndDate())
                .createdAt(maintenance.getCreatedAt())
                .updatedAt(maintenance.getUpdatedAt())
                .build();

    }

}