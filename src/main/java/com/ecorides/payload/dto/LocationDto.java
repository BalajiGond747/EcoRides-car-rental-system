package com.ecorides.payload.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {

    private Long id;
    private String name;
    private String city;
    private String address;
    private String pincode;
    private Boolean active;
}