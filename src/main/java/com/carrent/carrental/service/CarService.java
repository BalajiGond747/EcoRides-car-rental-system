package com.carrent.carrental.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carrent.carrental.dto.CarDTO;
import com.carrent.carrental.entity.Car;
import com.carrent.carrental.mappers.CarMapper;
import com.carrent.carrental.repository.CarRepository;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public CarDTO createCar(CarDTO carDTO) {
        Car car = CarMapper.toEntity(carDTO);
        Car savedCar = carRepository.save(car);
        return CarMapper.toDTO(savedCar);
    }

    public List<CarDTO> getAllCars() {
        return carRepository.findAll()
        .stream()
        .map(CarMapper::toDTO)
        .toList();
    }

    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        return CarMapper.toDTO(car);
    }

    public CarDTO updateCar(Long id, CarDTO carDTO) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        existingCar.setCarType(carDTO.getCarType());
        existingCar.setBasePricePerDay(carDTO.getBasePricePerDay());
        existingCar.setCurrentPrice(carDTO.getCurrentPrice());
        existingCar.setBatteryLevel(carDTO.getBatteryLevel());
        existingCar.setRange(carDTO.getRange());
        existingCar.setAvailable(carDTO.isAvailable());
        existingCar.setSeatingCapacity(carDTO.getSeatingCapacity());
        existingCar.setCharging(carDTO.isCharging());
        existingCar.setName(carDTO.getName());
        existingCar.setRegistrationNumber(carDTO.getRegistrationNumber());
        existingCar.setImageUrl(carDTO.getImageUrl());

        Car updatedCar = carRepository.save(existingCar);
        return CarMapper.toDTO(updatedCar);
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id))
            throw new RuntimeException("Car not found");
        carRepository.deleteById(id);
    }
}
