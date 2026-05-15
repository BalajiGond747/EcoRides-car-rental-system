package com.ecorides.mappers;

import com.ecorides.entity.Booking;
import com.ecorides.entity.Car;
import com.ecorides.entity.User;
import com.ecorides.payload.dto.BookingDto;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    private BookingMapper() {}


    public static BookingDto toDto(Booking booking) {
        if (booking == null) return null;

        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .carId(booking.getCar().getId())
                .locationId(
                        booking.getLocation() != null ? booking.getLocation().getId() : null
                )
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }


    public static List<BookingDto> toDtoList(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) return List.of();

        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }


    public static Booking toEntity(BookingDto dto, User user, Car car) {
        if (dto == null) return null;

        return Booking.builder()
                .user(user)
                .car(car)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())

                .totalAmount(0.0)
                .status(null)
                .build();
    }


    public static void updateEntity(Booking booking, BookingDto dto) {
        if (booking == null || dto == null) return;


        if (dto.getStartTime() != null) {
            booking.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            booking.setEndTime(dto.getEndTime());
        }


    }


}