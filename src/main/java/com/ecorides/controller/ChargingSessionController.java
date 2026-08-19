package com.ecorides.controller;

import com.ecorides.payload.dto.ChargingSessionDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/charging")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChargingSessionController {

    private final ChargingSessionService service;

    @PostMapping("/start/{carId}")
    public ResponseEntity<ApiResponse<ChargingSessionDTO>> startCharging(@PathVariable Long carId) {

        ChargingSessionDTO session = service.startCharging(carId);

        return ResponseEntity.ok(ApiResponse.<ChargingSessionDTO>builder()
                .success(true)
                .message("Charging started successfully")
                .data(session)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/end/{sessionId}")
    public ResponseEntity<ApiResponse<ChargingSessionDTO>> endCharging(@PathVariable Long sessionId, @RequestParam BigDecimal chargeAdded) {

        ChargingSessionDTO session = service.endCharging(sessionId, chargeAdded);

        return ResponseEntity.ok(ApiResponse.<ChargingSessionDTO>builder()
                .success(true)
                .message("Charging completed successfully")
                .data(session)
                .timestamp(LocalDateTime.now())
                .build());
    }

}