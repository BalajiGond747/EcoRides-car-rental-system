package com.carrent.carrental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrent.carrental.entity.Car;


public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByIsAvailableTrue();

    List<Car> findByCarType(String carType);

    List<Car> findByBatteryLevelGreaterThanEqual(int minBattery);
}