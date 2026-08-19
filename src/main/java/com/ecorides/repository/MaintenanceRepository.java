package com.ecorides.repository;

import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    boolean existsByCarIdAndStatusIn(Long carId, List<MaintenanceStatus> statuses);

    @Query("""
            SELECT COUNT(m) > 0
            FROM Maintenance m
            WHERE m.car.id = :carId
            AND m.status IN :statuses
            AND m.startDate < :endDate
            AND m.endDate > :startDate
            """)
    boolean existsOverlappingMaintenance(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("statuses") List<MaintenanceStatus> statuses);
}