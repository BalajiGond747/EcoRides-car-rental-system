package com.ecorides.repository;

import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    boolean existsByCarIdAndStatusIn(Long carId, List<MaintenanceStatus> statuses);
}