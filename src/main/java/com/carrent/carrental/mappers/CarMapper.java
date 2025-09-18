package com.carrent.carrental.mappers;

import java.time.LocalDate;

import com.carrent.carrental.dto.CarDTO;
import com.carrent.carrental.entity.Car;

public class CarMapper {

    // Entity -> DTO
    public static CarDTO toDTO(Car car) {
        if (car == null) return null;

        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setName(car.getName());
        dto.setCarType(car.getCarType());
        dto.setRegistrationNumber(car.getRegistrationNumber());
        dto.setBasePricePerDay(car.getBasePricePerDay());
        dto.setCurrentPrice(car.getCurrentPrice());
        dto.setBatteryLevel(car.getBatteryLevel());
        dto.setRange(car.getRange());
        dto.setAvailable(car.isAvailable());
        dto.setSeatingCapacity(car.getSeatingCapacity());
        dto.setCharging(car.isCharging());
        dto.setImageUrl(car.getImageUrl());

        return dto;
    }

    // DTO -> Entity
   // DTO -> Entity
public static Car toEntity(CarDTO dto) {
    if (dto == null) return null;

    Car car = new Car();
    car.setId(dto.getId());
    car.setName(dto.getName());
    car.setCarType(dto.getCarType());
    car.setRegistrationNumber(dto.getRegistrationNumber());
    car.setBasePricePerDay(dto.getBasePricePerDay());
    car.setCurrentPrice(dto.getCurrentPrice());
    car.setBatteryLevel(dto.getBatteryLevel());
    car.setRange(dto.getRange());
    car.setAvailable(dto.isAvailable());
    car.setSeatingCapacity(dto.getSeatingCapacity());
    car.setCharging(dto.isCharging());
    car.setImageUrl(dto.getImageUrl());

    // map lastServiceDate
    car.setLastServiceDate(dto.getLastServiceDate() != null ? dto.getLastServiceDate() : LocalDate.now());

    return car;
}

}
