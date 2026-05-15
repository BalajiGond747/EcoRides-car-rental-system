package com.ecorides.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingSessionDto {

    private Long id;
    private Long carId;
    private String startTime;
    private String endTime;
    private Double chargeAdded;
}