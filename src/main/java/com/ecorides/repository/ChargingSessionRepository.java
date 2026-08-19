package com.ecorides.repository;

import com.ecorides.entity.ChargingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    boolean existsByCarIdAndEndTimeIsNull(Long carId);

    List<ChargingSession> findByCarId(Long carId);

}