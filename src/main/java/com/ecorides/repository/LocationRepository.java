package com.ecorides.repository;

import com.ecorides.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCityIgnoreCase(String city);

    List<Location> findByActiveTrue();
}