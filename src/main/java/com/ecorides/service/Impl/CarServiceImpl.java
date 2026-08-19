package com.ecorides.service.Impl;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import com.ecorides.entity.Location;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.CarException;
import com.ecorides.exception.ConflictException;
import com.ecorides.exception.ResourceNotFoundException;
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

        String registrationNumber = dto.getRegistrationNumber()
                .trim()
                .toUpperCase();

        if (carRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new ConflictException("Car with this registration already exists");
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        dto.setRegistrationNumber(registrationNumber);

        Car car = CarMapper.toEntity(dto);
        car.setLocation(location);
        Car saved = carRepository.save(car);

        return CarMapper.toDTO(saved);
    }

    @Override
    public CarDTO updateCar(Long carId, CarDTO dto) {

        Car car = getCar(carId);

        if (dto.getRegistrationNumber() != null) {

            String registrationNumber = dto.getRegistrationNumber()
                    .trim()
                    .toUpperCase();

            if (!registrationNumber.equals(car.getRegistrationNumber()) && carRepository.existsByRegistrationNumber(registrationNumber)) {

                throw new ConflictException("Registration number already exists");
            }

            dto.setRegistrationNumber(registrationNumber);
        }

        if (dto.getLocationId() != null) {

            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

            car.setLocation(location);
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
    public CarDTO updateCarStatus(Long carId, CarStatus status) {

        if (status == null) {
            throw new CarException("Car status cannot be null");
        }

        Car car = getCar(carId);
        car.setStatus(status);
        carStatusLogService.logStatus(car, status);

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

        return CarMapper.toDTOList(carRepository.findByIsActiveTrueAndStatus(CarStatus.AVAILABLE));
    }

    @Override
    public List<CarDTO> getCarsByLocation(Long locationId) {

        if (locationId == null || locationId <= 0) {
            throw new BadRequestException("Invalid location id");
        }

        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Location not found with id: " + locationId);
        }

        return CarMapper.toDTOList(carRepository.findByLocationIdAndIsActiveTrue(locationId));
    }

    @Override
    public List<CarDTO> getCarsByCategory(String category) {

        if (category == null || category.isBlank()) {
            throw new BadRequestException("Category cannot be empty");
        }

        return CarMapper.toDTOList(carRepository.findByCategoryAndIsActiveTrue(category.trim()));
    }

    private Car getCar(Long carId) {

        if (carId == null || carId <= 0) {
            throw new BadRequestException("Invalid car id");
        }

        return carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
    }
}