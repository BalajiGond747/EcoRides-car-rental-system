package com.ecorides.repository;

import com.ecorides.entity.CarStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarStatusLogRepository extends JpaRepository<CarStatusLog, Long> {

    List<CarStatusLog> findByCarIdOrderByUpdatedAtDesc(Long carId);

}