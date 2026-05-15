package com.ecorides.mappers;

import com.ecorides.entity.ChargingSession;
import com.ecorides.payload.dto.ChargingSessionDto;

public class ChargingSessionMapper {

    private ChargingSessionMapper() {}

    public static ChargingSessionDto toDto(ChargingSession session) {
        if (session == null) return null;

        return ChargingSessionDto.builder()
                .id(session.getId())
                .carId(session.getCar().getId())
                .startTime(
                        session.getStartTime() != null ? session.getStartTime().toString() : null
                )
                .endTime(
                        session.getEndTime() != null ? session.getEndTime().toString() : null
                )
                .chargeAdded(session.getChargeAdded())
                .build();
    }
}