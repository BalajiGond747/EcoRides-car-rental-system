package com.ecorides.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {

    private Long id;

    @NotBlank(message = "Location name required")
    private String name;

    @NotBlank(message = "City required")
    private String city;

    @NotBlank(message = "State required")
    private String state;

    @NotBlank(message = "Address required")
    private String address;

    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
    private String pincode;

    private Boolean isActive;

}