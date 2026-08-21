package com.ecorides.service.Impl;

import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.CarStatus;
import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.*;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.mappers.BookingMapper;
import com.ecorides.payload.dto.BookingDto;
import com.ecorides.repository.*;
import com.ecorides.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final LocationRepository locationRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final CouponRepository couponRepository;

    @Override
    public BookingDto createBooking(BookingDto dto) {

        User user = getAuthenticatedUser();

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + dto.getCarId()));

        boolean underMaintenance = maintenanceRepository.existsByCarIdAndStatusIn(car.getId(), List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS));

        if (underMaintenance) {
            throw new BadRequestException("Car is currently under maintenance");
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + dto.getLocationId()));

        if (!car.getLocation()
                .getId()
                .equals(location.getId())) {
            throw new BadRequestException("Car does not belong to the selected location");
        }

        Coupon coupon = null;

        if (dto.getCouponCode() != null && !dto.getCouponCode()
                .isBlank()) {

            coupon = couponRepository.findById(dto.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + dto.getCouponCode()));
        }
        
        validateTime(dto.getStartTime(), dto.getEndTime());
        validateCar(car);

        boolean overlap = bookingRepository.existsOverlappingBooking(car.getId(), dto.getStartTime(), dto.getEndTime(), List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED));

        if (overlap) {
            throw new BadRequestException("Car already booked for the selected time");
        }

        Booking booking = BookingMapper.toEntity(dto, user, car, location, coupon);

        BigDecimal amount = calculatePrice(car, dto.getStartTime(), dto.getEndTime());

        if (coupon != null) {
            amount = applyCoupon(amount, coupon);
        }

        booking.setTotalAmount(amount);
        booking.setBookingReference(generateBookingReference());
        booking.setStatus(BookingStatus.PENDING);
        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId) {

        Booking booking = getBookingEntity(bookingId);

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !booking.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException("You do not have permission to access this booking");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {

        return BookingMapper.toDtoList(bookingRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Long userId) {

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !currentUser.getId()
                .equals(userId)) {

            throw new AccessDeniedException("You do not have permission to access these bookings");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return BookingMapper.toDtoList(bookingRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByCar(Long carId) {

        return BookingMapper.toDtoList(bookingRepository.findByCarId(carId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByLocation(Long locationId) {

        return BookingMapper.toDtoList(bookingRepository.findByLocationId(locationId));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, BookingDto dto) {

        Booking booking = getBookingEntity(bookingId);

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !booking.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException("You do not have permission to update this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking cannot be updated");
        }

        validateTime(dto.getStartTime(), dto.getEndTime());

        boolean overlap = bookingRepository.existsOverlappingBooking(booking.getCar()
                .getId(), dto.getStartTime(), dto.getEndTime(), List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED));

        if (overlap) {
            throw new BadRequestException("Car already booked for the selected time");
        }

        BookingMapper.updateEntity(booking, dto);

        BigDecimal amount = calculatePrice(booking.getCar(), booking.getStartTime(), booking.getEndTime());

        booking.setTotalAmount(amount);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void cancelBooking(Long bookingId, String reason) {

        Booking booking = getBookingEntity(bookingId);

        User currentUser = getAuthenticatedUser();

        if (currentUser.getUserRole() != UserRole.ADMIN && !booking.getUser()
                .getId()
                .equals(currentUser.getId())) {

            throw new AccessDeniedException("You do not have permission to update this booking");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Completed booking cannot be cancelled");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }
        if (reason == null || reason.isBlank()) {
            throw new BadRequestException("Cancellation reason is required");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);

        bookingRepository.save(booking);
    }

    @Override
    public BookingDto startBooking(Long bookingId) {

        Booking booking = getBookingEntity(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Booking not confirmed");
        }

        booking.setStatus(BookingStatus.ACTIVE);

        booking.getCar()
                .setStatus(CarStatus.IN_USE);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto completeBooking(Long bookingId) {

        Booking booking = getBookingEntity(bookingId);

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new BadRequestException("Booking not active");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        booking.getCar()
                .setStatus(CarStatus.AVAILABLE);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    private Booking getBookingEntity(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid booking id");
        }

        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null) {
            throw new BadRequestException("Start time and end time are required");
        }

        if (!start.isBefore(end)) {
            throw new BadRequestException("End time must be after start time");
        }

        if (!start.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Booking start time must be in the future");
        }
    }

    private void validateCar(Car car) {

        if (!Boolean.TRUE.equals(car.getIsActive())) {
            throw new BadRequestException("Car is inactive");
        }

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new BadRequestException("Car is not available");
        }

        if (car.getBatteryLevel() < 20) {
            throw new BadRequestException("Car battery is too low");
        }
    }

    private BigDecimal calculatePrice(Car car, LocalDateTime start, LocalDateTime end) {

        long hours = Duration.between(start, end)
                .toHours();

        long days = (hours + 23) / 24;

        return car.getPricePerDay()
                .multiply(BigDecimal.valueOf(days));
    }

    private BigDecimal applyCoupon(BigDecimal amount, Coupon coupon) {

        return amount;
    }

    private String generateBookingReference() {

        return "ECO-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName()
                .isBlank()) {

            throw new AccessDeniedException("Authenticated user not found");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}