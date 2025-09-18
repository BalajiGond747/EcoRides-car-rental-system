package com.carrent.carrental.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carrent.carrental.dto.BookingDTO;
import com.carrent.carrental.entity.Booking;
import com.carrent.carrental.entity.Car;
import com.carrent.carrental.entity.User;
import com.carrent.carrental.enums.BookingStatus;
import com.carrent.carrental.mappers.BookingMapper;
import com.carrent.carrental.repository.BookingRepository;
import com.carrent.carrental.repository.CarRepository;
import com.carrent.carrental.repository.UserRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public BookingService(BookingRepository bookingRepository,UserRepository userRepository,CarRepository carRepository) {
        this.bookingRepository = bookingRepository;
        this.carRepository=carRepository;
        this.userRepository=userRepository;
    }

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        // Load user and car from their repositories
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Car car = carRepository.findById(bookingDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));
    
        Booking booking = BookingMapper.toEntity(bookingDTO, user, car);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDTO(savedBooking);
    }
    

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper::toDTO)
                .toList();
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return BookingMapper.toDTO(booking);
    }

    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existingBooking.setStartDate(bookingDTO.getStartDate());
        existingBooking.setEndDate(bookingDTO.getEndDate());
        existingBooking.setStatus(BookingStatus.valueOf(bookingDTO.getStatus()));

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return BookingMapper.toDTO(updatedBooking);
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id))
            throw new RuntimeException("Booking not found");
        bookingRepository.deleteById(id);
    }
}
