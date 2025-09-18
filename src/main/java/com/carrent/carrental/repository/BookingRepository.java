package com.carrent.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrent.carrental.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByCarId(Long carId);

    List<Booking> findByStatus(String status);
}