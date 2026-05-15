package com.ecorides.payload.dto;

import com.ecorides.domain.CarStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {

    private Long id;

    private String name;

    private String category;

    private String registrationNumber;

    @Positive(message = "Price must be positive")
    private Double pricePerDay;

    @Min(value = 0, message = "Battery cannot be less than 0")
    @Max(value = 100, message = "Battery cannot exceed 100")
    private Integer batteryLevel;

    @Positive(message = "Range must be positive")
    private Integer rangeKm;

    private CarStatus status;

    private Long locationId;

    @Positive(message = "Seating capacity must be positive")
    private int seatingCapacity;

    private String imageUrl;

    private Boolean isActive;
}