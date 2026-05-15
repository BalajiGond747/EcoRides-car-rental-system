package com.ecorides.controller;


import com.ecorides.payload.dto.CarDTO;
import com.ecorides.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;


    @PostMapping
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carService.createCar(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarDTO dto) {

        return ResponseEntity.ok(carService.updateCar(id, dto));
    }


    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateCar(@PathVariable Long id) {
        carService.activateCar(id);
        return ResponseEntity.ok(Map.of("message", "Car activated"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateCar(@PathVariable Long id) {
        carService.deactivateCar(id);
        return ResponseEntity.ok(Map.of("message", "Car deactivated"));
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<CarDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(carService.updateCarStatus(id, status));
    }


    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }


    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }


    @GetMapping("/available")
    public ResponseEntity<List<CarDTO>> getAvailableCars() {
        return ResponseEntity.ok(carService.getAvailableCars());
    }


    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<CarDTO>> getByLocation(
            @PathVariable Long locationId) {

        return ResponseEntity.ok(carService.getCarsByLocation(locationId));
    }
}