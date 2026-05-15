package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.CarStatusLog;
import com.ecorides.repository.CarStatusLogRepository;
import com.ecorides.service.CarStatusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CarStatusLogServiceImpl implements CarStatusLogService {

    private final CarStatusLogRepository repository;

    @Override
    public void logStatus(Car car, CarStatus status) {

        CarStatusLog log = CarStatusLog.builder()
                .carId(car.getId())
                .status(status)
                .updatedAt(LocalDateTime.now())
                .build();

        repository.save(log);
    }
}