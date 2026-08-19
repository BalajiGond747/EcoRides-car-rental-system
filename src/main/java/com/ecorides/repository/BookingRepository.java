package com.ecorides.repository;

import com.ecorides.domain.BookingStatus;
import com.ecorides.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            
            SELECT COUNT(b)>0
            
            FROM Booking b
            
            WHERE b.car.id=:carId
            
            AND b.status
            NOT IN :excludedStatuses
            
            AND b.startTime<:endTime
            
            AND b.endTime>:startTime
            
            """)
    boolean existsOverlappingBooking(

            @Param("carId") Long carId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("excludedStatuses") List<BookingStatus> excludedStatuses

    );

    List<Booking> findByUserId(Long userId);

    List<Booking> findByCarId(Long carId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByCarIdAndStatusIn(Long carId, List<BookingStatus> statuses);

    List<Booking> findByLocationId(Long locationId);

    Optional<Booking> findByBookingReference(String bookingReference);

}