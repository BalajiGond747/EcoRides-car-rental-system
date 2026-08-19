package com.ecorides.mappers;

import com.ecorides.domain.BookingStatus;
import com.ecorides.entity.*;
import com.ecorides.payload.dto.BookingDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {

        if (booking == null) {
            return null;
        }

        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUser()
                        .getId())
                .carId(booking.getCar()
                        .getId())
                .locationId(booking.getLocation() != null ? booking.getLocation()
                        .getId() : null)
                .couponCode(booking.getCoupon() != null ? booking.getCoupon()
                        .getCode() : null)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .bookingReference(booking.getBookingReference())
                .cancellationReason(booking.getCancellationReason())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    public static List<BookingDto> toDtoList(List<Booking> bookings) {

        if (bookings == null || bookings.isEmpty()) {
            return List.of();
        }

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Booking toEntity(BookingDto dto, User user, Car car, Location location, Coupon coupon) {

        if (dto == null) {
            return null;
        }

        return Booking.builder()
                .user(user)
                .car(car)
                .location(location)
                .coupon(coupon)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .totalAmount(BigDecimal.ZERO)
                .status(BookingStatus.PENDING)
                .build();
    }

    public static void updateEntity(Booking booking, BookingDto dto) {

        if (booking == null || dto == null) {
            return;
        }

        if (dto.getStartTime() != null) {
            booking.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            booking.setEndTime(dto.getEndTime());
        }

        if (dto.getCancellationReason() != null) {
            booking.setCancellationReason(dto.getCancellationReason());
        }
    }
}