package com.ecorides.repository;

import com.ecorides.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCityIgnoreCase(String city);

    List<Location> findByIsActiveTrue();

    Optional<Location> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}