package com.ecorides.repository;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Car> findByIsActiveTrue();

    List<Car> findByIsActiveTrueAndStatus(CarStatus status);

    List<Car> findByLocationIdAndIsActiveTrue(Long locationId);

    List<Car> findByLocationIdAndStatusAndIsActiveTrue(Long locationId, CarStatus status);

    List<Car> findByCategoryAndIsActiveTrue(String category);
}