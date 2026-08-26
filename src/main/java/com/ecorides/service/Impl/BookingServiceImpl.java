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
import com.ecorides.payload.response.PageResponse;
import com.ecorides.repository.*;
import com.ecorides.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

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

        validateTime(dto.getStartTime(), dto.getEndTime());

        validateCar(car);

        boolean overlap = bookingRepository.existsOverlappingBooking(car.getId(), dto.getStartTime(), dto.getEndTime(), List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED));

        if (overlap) {
            throw new BadRequestException("Car already booked for the selected time");
        }

        Coupon coupon = null;

        if (dto.getCouponCode() != null && !dto.getCouponCode()
                .isBlank()) {

            coupon = couponRepository.findById(dto.getCouponCode()
                            .trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + dto.getCouponCode()));
        }

        long rentalDays = calculateRentalDays(dto.getStartTime(), dto.getEndTime());

        BigDecimal rentalAmount = car.getPricePerDay()
                .multiply(BigDecimal.valueOf(rentalDays));

        BigDecimal discountAmount = calculateCouponDiscount(rentalAmount, coupon);

        BigDecimal amountAfterDiscount = rentalAmount.subtract(discountAmount);

        BigDecimal taxAmount = amountAfterDiscount.multiply(GST_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = amountAfterDiscount.add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        Booking booking = BookingMapper.toEntity(dto, user, car, location, coupon);

        booking.setRentalAmount(rentalAmount);
        booking.setDiscountAmount(discountAmount);
        booking.setTaxAmount(taxAmount);
        booking.setTotalAmount(totalAmount);
        booking.setBookingReference(generateBookingReference());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);

        return toDto(saved, rentalDays);
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

        return toDto(booking, calculateRentalDays(booking.getStartTime(), booking.getEndTime()));
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
            throw new ResourceNotFoundException("User not found");
        }

        return bookingRepository.findByUserId(userId)
                .stream()
                .map(booking -> toDto(booking, calculateRentalDays(booking.getStartTime(), booking.getEndTime())))
                .toList();
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

        long rentalDays = calculateRentalDays(dto.getStartTime(), dto.getEndTime());

        BigDecimal rentalAmount = booking.getCar()
                .getPricePerDay()
                .multiply(BigDecimal.valueOf(rentalDays));

        BigDecimal discountAmount = calculateCouponDiscount(rentalAmount, booking.getCoupon());

        BigDecimal amountAfterDiscount = rentalAmount.subtract(discountAmount);

        BigDecimal taxAmount = amountAfterDiscount.multiply(GST_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = amountAfterDiscount.add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        BookingMapper.updateEntity(booking, dto);

        booking.setRentalAmount(rentalAmount);
        booking.setDiscountAmount(discountAmount);
        booking.setTaxAmount(taxAmount);
        booking.setTotalAmount(totalAmount);

        Booking saved = bookingRepository.save(booking);

        return toDto(saved, rentalDays);
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

        return toDto(bookingRepository.save(booking), calculateRentalDays(booking.getStartTime(), booking.getEndTime()));
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

        return toDto(bookingRepository.save(booking), calculateRentalDays(booking.getStartTime(), booking.getEndTime()));
    }

    private long calculateRentalDays(LocalDateTime start, LocalDateTime end) {

        long days = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());

        return Math.max(days, 1);
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

    private BigDecimal calculateCouponDiscount(BigDecimal rentalAmount, Coupon coupon) {

        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        if (coupon.getType() == null || coupon.getValue() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;

        switch (coupon.getType()) {

            case PERCENTAGE:
                discount = rentalAmount.multiply(coupon.getValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;

            case FLAT:
                discount = coupon.getValue();
                break;

            default:
                discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(rentalAmount) > 0) {
            discount = rentalAmount;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private BookingDto toDto(Booking booking, long rentalDays) {

        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUser() != null ? booking.getUser()
                        .getId() : null)
                .carId(booking.getCar() != null ? booking.getCar()
                        .getId() : null)
                .carName(booking.getCar() != null ? booking.getCar()
                        .getName() : null)
                .locationId(booking.getLocation() != null ? booking.getLocation()
                        .getId() : null)
                .locationName(booking.getLocation() != null ? booking.getLocation()
                        .getName() : null)
                .couponCode(booking.getCoupon() != null ? booking.getCoupon()
                        .getCode() : null)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .rentalDays(rentalDays)
                .rentalAmount(booking.getRentalAmount())
                .discountAmount(booking.getDiscountAmount())
                .taxAmount(booking.getTaxAmount())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .bookingReference(booking.getBookingReference())
                .cancellationReason(booking.getCancellationReason())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private Booking getBookingEntity(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid booking id");
        }

        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingDto> getAllBookings(int page, int size, String search, BookingStatus status, String sortBy, String sortDir) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Set<String> allowedSortFields = Set.of("id", "bookingReference", "startTime", "endTime", "totalAmount", "status", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        boolean hasSearch = search != null && !search.trim()
                .isEmpty();

        Page<Booking> bookingPage;

        if (hasSearch && status != null) {

            bookingPage = bookingRepository.findByStatusAndBookingReferenceContainingIgnoreCase(status, search.trim(), pageable);

        } else if (hasSearch) {

            bookingPage = bookingRepository.findByBookingReferenceContainingIgnoreCase(search.trim(), pageable);

        } else if (status != null) {

            bookingPage = bookingRepository.findByStatus(status, pageable);

        } else {

            bookingPage = bookingRepository.findAll(pageable);
        }

        List<BookingDto> bookings = BookingMapper.toDtoList(bookingPage.getContent());

        return PageResponse.<BookingDto>builder()
                .content(bookings)
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .totalElements(bookingPage.getTotalElements())
                .totalPages(bookingPage.getTotalPages())
                .first(bookingPage.isFirst())
                .last(bookingPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByCar(Long carId) {

        if (carId == null || carId <= 0) {
            throw new BadRequestException("Invalid car id");
        }

        if (!carRepository.existsById(carId)) {
            throw new ResourceNotFoundException("Car not found with id: " + carId);
        }

        return bookingRepository.findByCarId(carId)
                .stream()
                .map(booking -> toDto(booking, calculateRentalDays(booking.getStartTime(), booking.getEndTime())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByLocation(Long locationId) {

        if (locationId == null || locationId <= 0) {
            throw new BadRequestException("Invalid location id");
        }

        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Location not found with id: " + locationId);
        }

        return bookingRepository.findByLocationId(locationId)
                .stream()
                .map(booking -> toDto(booking, calculateRentalDays(booking.getStartTime(), booking.getEndTime())))
                .toList();
    }
}