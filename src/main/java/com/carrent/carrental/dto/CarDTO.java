package com.carrent.carrental.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {

    private Long id;

    @NotBlank(message = "Car name is required")
    @Size(max = 100, message = "Car name must be under 100 characters")
    private String name;

    @NotBlank(message = "Car type is required")
    @Size(max = 50, message = "Car type must be under 50 characters")
    private String carType;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z0-9-]{6,20}$", message = "Invalid registration number format")
    private String registrationNumber;

    @Positive(message = "Base price must be greater than 0")
    private double basePricePerDay;

    @PositiveOrZero(message = "Current price cannot be negative")
    private double currentPrice;

    @Min(value = 0, message = "Battery level must be >= 0")
    @Max(value = 100, message = "Battery level must be <= 100")
    private int batteryLevel;

    @PositiveOrZero(message = "Range cannot be negative")
    private int range;

    private boolean isAvailable = true;

    @Positive(message = "Seating capacity must be greater than 0")
    private int seatingCapacity;

    private boolean isCharging = false;

    @Size(max = 255, message = "Image URL must be under 255 characters")
    private String imageUrl;

    @PastOrPresent(message = "Last service date cannot be in the future")
    private LocalDate lastServiceDate;
}
