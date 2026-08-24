package com.ecorides.repository;

import com.ecorides.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCityIgnoreCase(String city);

    List<Location> findByIsActiveTrue();

    Optional<Location> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Page<Location> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("""
            SELECT l FROM Location l
            WHERE
                LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(l.city) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(l.state) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(l.address) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Location> findBySearch(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT l FROM Location l
            WHERE l.isActive = :isActive
            AND (
                LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(l.city) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(l.state) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(l.address) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<Location> findBySearchAndIsActive(@Param("search") String search, @Param("isActive") Boolean isActive, Pageable pageable);
}