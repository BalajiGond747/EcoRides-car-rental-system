package com.ecorides.repository;

import com.ecorides.domain.CarStatus;
import com.ecorides.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
            SELECT c FROM Car c
            WHERE (
                LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<Car> findBySearch(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT c FROM Car c
            WHERE (
                LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND LOWER(c.category) = LOWER(:category)
            """)
    Page<Car> findBySearchAndCategory(@Param("search") String search, @Param("category") String category, Pageable pageable);

    @Query("""
            SELECT c FROM Car c
            WHERE (
                LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND LOWER(c.category) = LOWER(:category)
            AND c.status = :status
            """)
    Page<Car> findBySearchAndCategoryAndStatus(@Param("search") String search, @Param("category") String category, @Param("status") CarStatus status, Pageable pageable);

    @Query("""
            SELECT c FROM Car c
            WHERE (
                LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND LOWER(c.category) = LOWER(:category)
            AND c.status = :status
            AND c.isActive = :isActive
            """)
    Page<Car> findBySearchAndCategoryAndStatusAndIsActive(@Param("search") String search, @Param("category") String category, @Param("status") CarStatus status, @Param("isActive") Boolean isActive, Pageable pageable);

    Page<Car> findByCategory(String category, Pageable pageable);

    Page<Car> findByCategoryAndStatus(String category, CarStatus status, Pageable pageable);

    Page<Car> findByCategoryAndStatusAndIsActive(String category, CarStatus status, Boolean isActive, Pageable pageable);

    Page<Car> findByStatus(CarStatus status, Pageable pageable);

    Page<Car> findByStatusAndIsActive(CarStatus status, Boolean isActive, Pageable pageable);

    Page<Car> findByIsActive(Boolean isActive, Pageable pageable);

}