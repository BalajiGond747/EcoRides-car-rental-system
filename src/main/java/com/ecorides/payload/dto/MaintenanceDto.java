package com.ecorides.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDto {

    private Long id;
    private Long carId;
    private String type;
    private String description;
    private String status;
    private String startDate;
    private String endDate;
}