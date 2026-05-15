package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.ChargingSession;
import com.ecorides.mappers.ChargingSessionMapper;
import com.ecorides.payload.dto.ChargingSessionDto;
import com.ecorides.repository.CarRepository;
import com.ecorides.repository.ChargingSessionRepository;
import com.ecorides.service.CarStatusLogService;
import com.ecorides.service.ChargingSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargingSessionServiceImpl implements ChargingSessionService {

    private final ChargingSessionRepository repository;
    private final CarRepository carRepository;
    private final CarStatusLogService logService;

    @Override
    @Transactional
    public ChargingSessionDto startCharging(Long carId) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if (repository.existsByCarIdAndEndTimeIsNull(carId)) {
            throw new RuntimeException("Charging already in progress");
        }

        car.setStatus(CarStatus.CHARGING);
        logService.logStatus(car, CarStatus.CHARGING);

        ChargingSession session = ChargingSession.builder()
                .car(car)
                .startTime(LocalDateTime.now())
                .build();

        repository.save(session);

        return ChargingSessionMapper.toDto(session);
    }

    @Override
    @Transactional
    public ChargingSessionDto endCharging(Long sessionId, Double chargeAdded) {

        ChargingSession session = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setEndTime(LocalDateTime.now());
        session.setChargeAdded(chargeAdded);

        Car car = session.getCar();
        car.setStatus(CarStatus.AVAILABLE);

        logService.logStatus(car, CarStatus.AVAILABLE);

        repository.save(session);

        return ChargingSessionMapper.toDto(session);
    }
}