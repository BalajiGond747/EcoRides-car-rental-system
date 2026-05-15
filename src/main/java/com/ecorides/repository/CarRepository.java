package com.ecorides.repository;
import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Car> findByIsActiveTrue();

    List<Car> findByIsActiveTrueAndStatus(CarStatus status);

    List<Car> findByLocationIdAndIsActiveTrue(Long locationId);
}