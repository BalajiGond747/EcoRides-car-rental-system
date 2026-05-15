package com.ecorides.service;

import com.ecorides.payload.dto.ChargingSessionDto;

public interface ChargingSessionService {

    ChargingSessionDto startCharging(Long carId);

    ChargingSessionDto endCharging(Long sessionId, Double chargeAdded);
}