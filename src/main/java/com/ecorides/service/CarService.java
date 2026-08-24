package com.ecorides.service;

import com.ecorides.domain.CarStatus;
import com.ecorides.payload.dto.CarDTO;
import com.ecorides.payload.response.PageResponse;

import java.util.List;

public interface CarService {

    CarDTO createCar(CarDTO carDTO);

    CarDTO updateCar(Long carId, CarDTO carDTO);

    void activateCar(Long carId);

    void deactivateCar(Long carId);

    CarDTO updateCarStatus(Long carId, CarStatus status);

    CarDTO getCarById(Long carId);

    PageResponse<CarDTO> getAllCars(int page, int size, String search, String category, CarStatus status, Boolean isActive, String sortBy, String sortDir);

    List<CarDTO> getAvailableCars();

    List<CarDTO> getCarsByLocation(Long locationId);

    List<CarDTO> getCarsByCategory(String category);
}