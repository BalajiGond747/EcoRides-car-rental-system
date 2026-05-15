package com.ecorides.repository;

import com.ecorides.domain.BookingStatus;
import com.ecorides.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.car.id = :carId
        AND b.status NOT IN :excludedStatuses
        AND b.startTime < :endTime
        AND b.endTime > :startTime
    """)
    boolean existsOverlappingBooking(
            Long carId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<BookingStatus> excludedStatuses
    );


    List<Booking> findByUserId(Long userId);


    List<Booking> findByCarId(Long carId);


    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByCarIdAndStatusIn(Long carId, List<BookingStatus> statuses);
}