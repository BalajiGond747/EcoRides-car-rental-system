package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.domain.ChargingStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.ChargingSession;
import com.ecorides.exception.ChargingException;
import com.ecorides.mappers.ChargingSessionMapper;
import com.ecorides.payload.dto.ChargingSessionDTO;
import com.ecorides.repository.CarRepository;
import com.ecorides.repository.ChargingSessionRepository;
import com.ecorides.service.CarStatusLogService;
import com.ecorides.service.ChargingSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargingSessionServiceImpl implements ChargingSessionService {

    private final ChargingSessionRepository repository;
    private final CarRepository carRepository;
    private final CarStatusLogService logService;

    @Override
    public ChargingSessionDTO startCharging(Long carId) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ChargingException("Car not found"));

        if (!Boolean.TRUE.equals(car.getIsActive())) {
            throw new ChargingException("Car is inactive");
        }

        if (car.getStatus() == CarStatus.MAINTENANCE) {
            throw new ChargingException("Car is under maintenance");
        }

        if (car.getStatus() == CarStatus.IN_USE) {
            throw new ChargingException("Car is currently in use");
        }

        if (repository.existsByCarIdAndEndTimeIsNull(carId)) {
            throw new ChargingException("Charging already running");
        }

        car.setStatus(CarStatus.CHARGING);

        logService.logStatus(car, CarStatus.CHARGING);

        ChargingSession session = ChargingSession.builder()
                .car(car)
                .startTime(LocalDateTime.now())
                .status(ChargingStatus.CHARGING)
                .chargeAdded(BigDecimal.ZERO)
                .build();

        ChargingSession saved = repository.save(session);

        return ChargingSessionMapper.toDto(saved);
    }

    @Override
    public ChargingSessionDTO endCharging(Long sessionId, BigDecimal chargeAdded) {

        ChargingSession session = repository.findById(sessionId)
                .orElseThrow(() -> new ChargingException("Session not found"));

        if (session.getEndTime() != null) {
            throw new ChargingException("Already completed");
        }

        if (chargeAdded == null || chargeAdded.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ChargingException("Charge added must be greater than zero");
        }

        int added;

        try {
            added = chargeAdded.intValueExact();
        } catch (ArithmeticException ex) {
            throw new ChargingException("Charge added must be a whole number");
        }

        Car car = session.getCar();

        int updatedBattery = Math.min(100, car.getBatteryLevel() + added);

        session.setEndTime(LocalDateTime.now());
        session.setChargeAdded(chargeAdded);
        session.setStatus(ChargingStatus.COMPLETED);

        car.setBatteryLevel(updatedBattery);
        car.setStatus(CarStatus.AVAILABLE);

        logService.logStatus(car, CarStatus.AVAILABLE);

        ChargingSession saved = repository.save(session);

        return ChargingSessionMapper.toDto(saved);
    }
}