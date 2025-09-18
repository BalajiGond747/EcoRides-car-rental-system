package com.carrent.carrental.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "Tata Nexon EV"

    @Column(nullable = false, length = 50)
    private String carType; // e.g., "Compact", "SUV", "Premium"

    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber; // unique car number plate

    @Column(nullable = false)
    private double basePricePerDay; // base rental price

    private double currentPrice; // price after dynamic calculation

    @Column(nullable = false)
    private int batteryLevel; // 0-100 %

    @Column(name = "range_in_km")
    private int range; // estimated km with full charge

    @Column(nullable = false)
    private boolean isAvailable = true; // availability status

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastServiceDate; // maintenance tracking

    @Column(nullable = false)
    private boolean isCharging = false; // is currently charging

    @Column(nullable = false)
    private int seatingCapacity; // passengers

    @Column(length = 255)
    private String imageUrl; // optional

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
}
