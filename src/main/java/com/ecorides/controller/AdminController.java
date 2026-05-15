package com.ecorides.controller;

import com.ecorides.domain.PaymentStatus;
import com.ecorides.entity.Payment;
import com.ecorides.mappers.BookingMapper;
import com.ecorides.mappers.CarMapper;
import com.ecorides.payload.dto.BookingDto;
import com.ecorides.payload.dto.CarDTO;
import com.ecorides.repository.BookingRepository;
import com.ecorides.repository.CarRepository;
import com.ecorides.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/bookings")
    public List<BookingDto> getAllBookings() {
        return BookingMapper.toDtoList(bookingRepository.findAll());
    }

    @GetMapping("/cars")
    public List<CarDTO> getAllCars() {
        return CarMapper.toDTOList(carRepository.findAll());
    }

    @GetMapping("/revenue")
    public double getTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}