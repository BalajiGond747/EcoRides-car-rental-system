package com.carrent.carrental.mappers;

import com.carrent.carrental.dto.BookingDTO;
import com.carrent.carrental.entity.Booking;
import com.carrent.carrental.entity.Car;
import com.carrent.carrental.entity.User;
import com.carrent.carrental.enums.BookingStatus;

public class BookingMapper {

    // Entity -> DTO
    public static BookingDTO toDTO(Booking booking) {
        if (booking == null) return null;

        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser() != null ? booking.getUser().getId() : null);
        dto.setCarId(booking.getCar() != null ? booking.getCar().getId() : null);
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);

        return dto;
    }

    // DTO -> Entity
    public static Booking toEntity(BookingDTO dto, User user, Car car) {
        if (dto == null) return null;

        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setUser(user);  // Pass User entity when calling
        booking.setCar(car);    // Pass Car entity when calling
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setTotalPrice(dto.getTotalPrice());
        booking.setStatus(dto.getStatus() != null ? BookingStatus.valueOf(dto.getStatus()) : null);

        return booking;
    }
}
