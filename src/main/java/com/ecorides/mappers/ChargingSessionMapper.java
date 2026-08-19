package com.ecorides.mappers;

import com.ecorides.entity.ChargingSession;
import com.ecorides.payload.dto.ChargingSessionDTO;

public class ChargingSessionMapper {

    public static ChargingSessionDTO toDto(ChargingSession session) {

        if (session == null) {
            return null;
        }

        return ChargingSessionDTO.builder()
                .id(session.getId())
                .carId(session.getCar() != null ? session.getCar()
                        .getId() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .chargeAdded(session.getChargeAdded())
                .status(session.getStatus())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();

    }

}