package com.ecorides.mappers;


import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import com.ecorides.payload.dto.CarDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {


    public static CarDTO toDTO(Car car) {
        if (car == null) return null;

        return CarDTO.builder()
                .id(car.getId())
                .name(car.getName())
                .category(car.getCategory())
                .registrationNumber(car.getRegistrationNumber())
                .pricePerDay(car.getPricePerDay())
                .batteryLevel(car.getBatteryLevel())
                .rangeKm(car.getRangeKm())
                .status(car.getStatus())
                .locationId(
                        car.getLocation() != null ? car.getLocation().getId() : null
                )
                .seatingCapacity(car.getSeatingCapacity())
                .imageUrl(car.getImageUrl())
                .isActive(car.getIsActive())
                .build();
    }


    public static List<CarDTO> toDTOList(List<Car> cars) {
        return cars.stream()
                .map(CarMapper::toDTO)
                .collect(Collectors.toList());
    }


    public static Car toEntity(CarDTO dto) {
        if (dto == null) return null;

        return Car.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .registrationNumber(dto.getRegistrationNumber())
                .pricePerDay(dto.getPricePerDay())
                .batteryLevel(dto.getBatteryLevel())
                .rangeKm(dto.getRangeKm())
                .seatingCapacity(dto.getSeatingCapacity())
                .imageUrl(dto.getImageUrl())
                .status(CarStatus.AVAILABLE)
                .isActive(true)
                .build();
    }


    public static void updateEntity(Car car, CarDTO dto) {
        if (car == null || dto == null) return;

        if (dto.getName() != null)
            car.setName(dto.getName());

        if (dto.getCategory() != null)
            car.setCategory(dto.getCategory());

        if (dto.getRegistrationNumber() != null)
            car.setRegistrationNumber(dto.getRegistrationNumber());

        if (dto.getBatteryLevel() != null)
            car.setBatteryLevel(dto.getBatteryLevel());

        if (dto.getRangeKm() != null)
            car.setRangeKm(dto.getRangeKm());

        if (dto.getPricePerDay() != null)
            car.setPricePerDay(dto.getPricePerDay());


        if (dto.getSeatingCapacity() > 0)
            car.setSeatingCapacity(dto.getSeatingCapacity());

        if (dto.getImageUrl() != null)
            car.setImageUrl(dto.getImageUrl());

        if (dto.getIsActive() != null)
            car.setIsActive(dto.getIsActive());


    }
}