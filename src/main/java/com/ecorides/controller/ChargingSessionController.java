package com.ecorides.controller;

import com.ecorides.payload.dto.ChargingSessionDto;
import com.ecorides.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charging")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChargingSessionController {

    private final ChargingSessionService service;

    @PostMapping("/start/{carId}")
    public ChargingSessionDto start(@PathVariable Long carId) {
        return service.startCharging(carId);
    }

    @PatchMapping("/end/{sessionId}")
    public ChargingSessionDto end(@PathVariable Long sessionId,
                                  @RequestParam Double chargeAdded) {
        return service.endCharging(sessionId, chargeAdded);
    }
}