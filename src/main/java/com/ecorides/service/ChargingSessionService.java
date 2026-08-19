package com.ecorides.service;

import com.ecorides.payload.dto.ChargingSessionDTO;

import java.math.BigDecimal;

public interface ChargingSessionService {

    ChargingSessionDTO startCharging(Long carId);

    ChargingSessionDTO endCharging(Long sessionId, BigDecimal chargeAdded);
}