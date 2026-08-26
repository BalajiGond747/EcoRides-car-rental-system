package com.ecorides.service.Impl;

import com.ecorides.domain.BookingStatus;
import com.ecorides.domain.CarStatus;
import com.ecorides.domain.CouponType;
import com.ecorides.domain.MaintenanceStatus;
import com.ecorides.entity.*;
import com.ecorides.exception.BadRequestException;
import com.ecorides.payload.dto.BookingDto;
import com.ecorides.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Car car;
    private Location location;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("customer@gmail.com");

        location = new Location();
        location.setId(10L);
        location.setName("Hitech City");

        car = new Car();
        car.setId(100L);
        car.setName("Tata Nexon EV");
        car.setPricePerDay(new BigDecimal("2500"));
        car.setIsActive(true);
        car.setStatus(CarStatus.AVAILABLE);
        car.setBatteryLevel(80);
        car.setLocation(location);

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("customer@gmail.com", null));

        when(userRepository.findByEmail("customer@gmail.com")).thenReturn(Optional.of(user));
    }

    @Test
    void shouldCreateBookingSuccessfullyWithoutCoupon() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingDto dto = createBookingDto(start, end, null);

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(false);

        when(locationRepository.findById(10L)).thenReturn(Optional.of(location));

        when(bookingRepository.existsOverlappingBooking(100L, start, end, List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED))).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.createBooking(dto);

        assertEquals(2L, result.getRentalDays());

        assertBigDecimalEquals("5000.00", result.getRentalAmount());

        assertBigDecimalEquals("0.00", result.getDiscountAmount());

        assertBigDecimalEquals("900.00", result.getTaxAmount());

        assertBigDecimalEquals("5900.00", result.getTotalAmount());

        assertEquals(BookingStatus.PENDING, result.getStatus());
    }

    @Test
    void shouldRejectBookingWhenCarIsUnderMaintenance() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingDto dto = createBookingDto(start, end, null);

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto));

        assertEquals("Car is currently under maintenance", exception.getMessage());
    }

    @Test
    void shouldRejectBookingWhenCarIsNotAvailable() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingDto dto = createBookingDto(start, end, null);

        car.setStatus(CarStatus.IN_USE);

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(false);

        when(locationRepository.findById(10L)).thenReturn(Optional.of(location));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto));

        assertEquals("Car is not available", exception.getMessage());
    }

    @Test
    void shouldRejectBookingWhenTimesAreInvalid() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(2);
        LocalDateTime end = start.minusHours(1);

        BookingDto dto = createBookingDto(start, end, null);

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(false);

        when(locationRepository.findById(10L)).thenReturn(Optional.of(location));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto));

        assertEquals("End time must be after start time", exception.getMessage());
    }

    @Test
    void shouldRejectOverlappingBooking() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingDto dto = createBookingDto(start, end, null);

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(false);

        when(locationRepository.findById(10L)).thenReturn(Optional.of(location));

        when(bookingRepository.existsOverlappingBooking(100L, start, end, List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED))).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto));

        assertEquals("Car already booked for the selected time", exception.getMessage());
    }

    @Test
    void shouldApplyPercentageCouponAndCalculateTotal() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingDto dto = createBookingDto(start, end, "SAVE10");

        Coupon coupon = new Coupon();
        coupon.setCode("SAVE10");
        coupon.setType(CouponType.PERCENTAGE);
        coupon.setValue(new BigDecimal("10"));
        coupon.setIsActive(true);
        coupon.setExpiryDate(start.toLocalDate()
                .plusDays(10));

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(false);

        when(locationRepository.findById(10L)).thenReturn(Optional.of(location));

        when(bookingRepository.existsOverlappingBooking(100L, start, end, List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED))).thenReturn(false);

        when(couponRepository.findById("SAVE10")).thenReturn(Optional.of(coupon));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.createBooking(dto);

        assertBigDecimalEquals("500.00", result.getDiscountAmount());

        assertBigDecimalEquals("4500.00", result.getRentalAmount()
                .subtract(result.getDiscountAmount()));

        assertBigDecimalEquals("810.00", result.getTaxAmount());

        assertBigDecimalEquals("5310.00", result.getTotalAmount());
    }

    @Test
    void shouldApplyFlatCouponAndCalculateTotal() {

        LocalDateTime start = LocalDateTime.now()
                .plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingDto dto = createBookingDto(start, end, "FLAT500");

        Coupon coupon = new Coupon();
        coupon.setCode("FLAT500");
        coupon.setType(CouponType.FLAT);
        coupon.setValue(new BigDecimal("500"));
        coupon.setIsActive(true);
        coupon.setExpiryDate(start.toLocalDate()
                .plusDays(10));

        when(carRepository.findById(100L)).thenReturn(Optional.of(car));

        when(maintenanceRepository.existsByCarIdAndStatusIn(100L, List.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS))).thenReturn(false);

        when(locationRepository.findById(10L)).thenReturn(Optional.of(location));

        when(bookingRepository.existsOverlappingBooking(100L, start, end, List.of(BookingStatus.CANCELLED, BookingStatus.COMPLETED))).thenReturn(false);

        when(couponRepository.findById("FLAT500")).thenReturn(Optional.of(coupon));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.createBooking(dto);

        assertBigDecimalEquals("500.00", result.getDiscountAmount());

        assertBigDecimalEquals("810.00", result.getTaxAmount());

        assertBigDecimalEquals("5310.00", result.getTotalAmount());
    }

    private BookingDto createBookingDto(LocalDateTime start, LocalDateTime end, String couponCode) {

        return BookingDto.builder()
                .carId(100L)
                .locationId(10L)
                .couponCode(couponCode)
                .startTime(start)
                .endTime(end)
                .build();
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {

        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}