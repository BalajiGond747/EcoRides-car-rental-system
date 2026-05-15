package com.ecorides.service.Impl;

import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.CarStatus;
import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.entity.*;
import com.ecorides.exception.BookingException;
import com.ecorides.mappers.BookingMapper;
import com.ecorides.payload.dto.BookingDto;
import com.ecorides.repository.*;
import com.ecorides.service.BookingService;
import com.ecorides.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final LocationRepository locationRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final CouponService couponService;


    @Override
    @Transactional
    public BookingDto createBooking(BookingDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BookingException("User not found"));

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new BookingException("Car not found"));

        if (car.getStatus() == CarStatus.CHARGING) {
            throw new BookingException("Car is currently charging");
        }

        if (maintenanceRepository.existsByCarIdAndStatusIn(
                car.getId(),
                List.of(MaintenanceStatus.IN_PROGRESS))) {
            throw new BookingException("Car is under maintenance");
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new BookingException("Location not found"));

        validateTime(dto.getStartTime(), dto.getEndTime());

        if (!Boolean.TRUE.equals(car.getIsActive())) {
            throw new BookingException("Car is inactive");
        }

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new BookingException("Car is not available");
        }

        boolean overlap = bookingRepository.existsOverlappingBooking(
                car.getId(),
                dto.getStartTime(),
                dto.getEndTime(),
                List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED)
        );

        if (overlap) {
            throw new BookingException("Car already booked for selected time");
        }

        validateEv(car, dto);


        Booking booking = BookingMapper.toEntity(dto, user, car);


        booking.setLocation(location);

        double totalAmount = calculatePrice(car, dto.getStartTime(), dto.getEndTime());

        if (dto.getCouponCode() != null && !dto.getCouponCode().isBlank()) {

            Coupon coupon = couponService.validateCoupon(dto.getCouponCode());

            if ("PERCENTAGE".equalsIgnoreCase(coupon.getType())) {
                totalAmount = totalAmount - (totalAmount * coupon.getValue() / 100);
            } else {
                totalAmount = totalAmount - coupon.getValue();
            }


            if (totalAmount < 0) {
                totalAmount = 0;
            }
        }
        booking.setTotalAmount(totalAmount);
        booking.setCouponCode(dto.getCouponCode());


        booking.setStatus(BookingStatus.CREATED);

        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toDto(saved);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        return BookingMapper.toDto(getBookingEntity(bookingId));
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return BookingMapper.toDtoList(bookingRepository.findAll());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId) {
        return BookingMapper.toDtoList(bookingRepository.findByUserId(userId));
    }

    @Override
    public List<BookingDto> getBookingsByCar(Long carId) {
        return BookingMapper.toDtoList(bookingRepository.findByCarId(carId));
    }


    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, BookingDto dto) {

        Booking booking = getBookingEntity(bookingId);

        if (booking.getStatus() != BookingStatus.CREATED &&
                booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Booking cannot be updated in current state");
        }

        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            validateTime(dto.getStartTime(), dto.getEndTime());

            boolean overlap = bookingRepository.existsOverlappingBooking(
                    booking.getCar().getId(),
                    dto.getStartTime(),
                    dto.getEndTime(),
                    List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED)
            );

            if (overlap) {
                throw new BookingException("Car already booked for selected time");
            }
        }

        BookingMapper.updateEntity(booking, dto);

        Car car = booking.getCar();

        double totalAmount = calculatePrice(car, dto.getStartTime(), dto.getEndTime());

        booking.setTotalAmount(totalAmount);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }


    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking = getBookingEntity(bookingId);

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BookingException("Cannot cancel completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }




    @Override
    @Transactional
    public BookingDto startBooking(Long bookingId) {

        Booking booking = getBookingEntity(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("Invalid status transition");
        }

        booking.setStatus(BookingStatus.IN_USE);
        booking.getCar().setStatus(CarStatus.IN_USE);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto completeBooking(Long bookingId) {

        Booking booking = getBookingEntity(bookingId);

        if (booking.getStatus() != BookingStatus.IN_USE) {
            throw new BookingException("Invalid status transition");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.getCar().setStatus(CarStatus.AVAILABLE);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }


    private Booking getBookingEntity(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found"));
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new BookingException("Invalid time range");
        }
    }

    private void validateEv(Car car, BookingDto dto) {
        if (car.getBatteryLevel() < 20) {
            throw new BookingException("Battery too low for booking");
        }
    }

    private double calculatePrice(Car car, LocalDateTime start, LocalDateTime end) {
        long hours = Duration.between(start, end).toHours();
        long days = (hours / 24) + 1;
        return days * car.getPricePerDay();
    }
}