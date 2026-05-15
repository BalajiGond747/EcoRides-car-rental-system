package com.ecorides.service.Impl;


import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.Location;
import com.ecorides.exception.CarException;
import com.ecorides.mappers.CarMapper;
import com.ecorides.payload.dto.CarDTO;
import com.ecorides.repository.CarRepository;
import com.ecorides.repository.LocationRepository;
import com.ecorides.service.CarService;
import com.ecorides.service.CarStatusLogService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final LocationRepository locationRepository;
    private final CarStatusLogService carStatusLogService;


    @Override
    public CarDTO createCar(CarDTO dto) {

        if (carRepository.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new CarException("Car with this registration already exists", 409);
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new CarException("Location not found",400));

        Car car = CarMapper.toEntity(dto);

        car.setLocation(location);   // 🔥 REQUIRED

        Car saved = carRepository.save(car);

        return CarMapper.toDTO(saved);
    }


    @Override
    public CarDTO updateCar(Long carId, CarDTO dto) {

        Car car = getCar(carId);



        if (dto.getRegistrationNumber() != null &&
                carRepository.existsByRegistrationNumber(dto.getRegistrationNumber()) &&
                !dto.getRegistrationNumber().equals(car.getRegistrationNumber())) {

            throw new CarException("Registration number already exists", 409);
        }
        CarMapper.updateEntity(car, dto);

        return CarMapper.toDTO(carRepository.save(car));
    }


    @Override
    public void activateCar(Long carId) {
        Car car = getCar(carId);
        car.setIsActive(true);
        carRepository.save(car);
    }

    @Override
    public void deactivateCar(Long carId) {
        Car car = getCar(carId);
        car.setIsActive(false);
        carRepository.save(car);
    }

    @Override
    public CarDTO updateCarStatus(Long carId, String status) {

        Car car = getCar(carId);

        CarStatus newStatus;
        try {
            newStatus = CarStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new CarException("Invalid car status", 400);
        }

        car.setStatus(newStatus);
        carStatusLogService.logStatus(car, newStatus);
        return CarMapper.toDTO(carRepository.save(car));
    }


    @Override
    public CarDTO getCarById(Long carId) {
        return CarMapper.toDTO(getCar(carId));
    }


    @Override
    public List<CarDTO> getAllCars() {
        return CarMapper.toDTOList(carRepository.findAll());
    }


    @Override
    public List<CarDTO> getAvailableCars() {
        return CarMapper.toDTOList(
                carRepository.findByIsActiveTrueAndStatus(CarStatus.AVAILABLE)
        );
    }


    @Override
    public List<CarDTO> getCarsByLocation(Long locationId) {
        return CarMapper.toDTOList(
                carRepository.findByLocationIdAndIsActiveTrue(locationId)
        );
    }


    private Car getCar(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new CarException("Car not found", 404));
    }
}