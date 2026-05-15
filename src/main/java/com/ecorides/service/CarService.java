package com.ecorides.service;

import com.ecorides.payload.dto.CarDTO;

import java.util.List;

public interface CarService {


    CarDTO createCar(CarDTO carDTO);


    CarDTO updateCar(Long carId, CarDTO carDTO);




    void activateCar(Long carId);

    void deactivateCar(Long carId);


    CarDTO updateCarStatus(Long carId, String status);


    CarDTO getCarById(Long carId);


    List<CarDTO> getAllCars();


    List<CarDTO> getAvailableCars();


    List<CarDTO> getCarsByLocation(Long locationId);
}