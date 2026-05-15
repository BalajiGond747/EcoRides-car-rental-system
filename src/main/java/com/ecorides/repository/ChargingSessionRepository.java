package com.ecorides.repository;

import com.ecorides.entity.ChargingSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    boolean existsByCarIdAndEndTimeIsNull(Long carId);
}