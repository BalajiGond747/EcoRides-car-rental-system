package com.ecorides.repository;

import com.ecorides.entity.CarStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarStatusLogRepository extends JpaRepository<CarStatusLog, Long> {
}