package com.ecorides.controller;

import com.ecorides.domain.BookingStatus;
import com.ecorides.payload.dto.BookingDto;
import com.ecorides.security.CustomUserDetailsService;
import com.ecorides.security.JwtAuthenticationFilter;
import com.ecorides.security.JwtUtil;
import com.ecorides.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldCreateBookingSuccessfully() throws Exception {

        BookingDto request = BookingDto.builder()
                .carId(100L)
                .locationId(10L)
                .startTime(LocalDateTime.now()
                        .plusDays(1))
                .endTime(LocalDateTime.now()
                        .plusDays(3))
                .build();

        BookingDto response = BookingDto.builder()
                .id(1L)
                .carId(100L)
                .locationId(10L)
                .carName("Tata Nexon EV")
                .locationName("Hitech City")
                .status(BookingStatus.PENDING)
                .build();

        when(bookingService.createBooking(any(BookingDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.carName").value("Tata Nexon EV"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void shouldGetBookingSuccessfully() throws Exception {

        BookingDto response = BookingDto.builder()
                .id(1L)
                .carId(100L)
                .locationId(10L)
                .carName("Tata Nexon EV")
                .status(BookingStatus.PENDING)
                .build();

        when(bookingService.getBookingById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.carName").value("Tata Nexon EV"));
    }

    @Test
    void shouldGetUserBookingsSuccessfully() throws Exception {

        BookingDto booking = BookingDto.builder()
                .id(1L)
                .userId(5L)
                .carName("Tata Nexon EV")
                .status(BookingStatus.PENDING)
                .build();

        when(bookingService.getBookingsByUser(5L)).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/bookings/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User bookings fetched"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].userId").value(5))
                .andExpect(jsonPath("$.data[0].carName").value("Tata Nexon EV"));
    }

    @Test
    void shouldCancelBookingSuccessfully() throws Exception {

        mockMvc.perform(patch("/api/bookings/1/cancel").param("reason", "Change of plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking cancelled"));

        verify(bookingService).cancelBooking(1L, "Change of plans");
    }

    @Test
    void shouldStartBookingSuccessfully() throws Exception {

        BookingDto response = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.ACTIVE)
                .build();

        when(bookingService.startBooking(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/bookings/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking started"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void shouldCompleteBookingSuccessfully() throws Exception {

        BookingDto response = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.COMPLETED)
                .build();

        when(bookingService.completeBooking(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/bookings/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking completed"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}