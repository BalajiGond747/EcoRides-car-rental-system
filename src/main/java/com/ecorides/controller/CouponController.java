package com.ecorides.controller;

import com.ecorides.payload.dto.CouponDTO;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CouponController {

    private final CouponService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponDTO>> createCoupon(@Valid @RequestBody CouponDTO dto) {

        CouponDTO coupon = service.createCoupon(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CouponDTO>builder()
                        .success(true)
                        .message("Coupon created successfully")
                        .data(coupon)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDTO>>> getAllCoupons() {

        return ResponseEntity.ok(ApiResponse.<List<CouponDTO>>builder()
                .success(true)
                .message("Coupons fetched successfully")
                .data(service.getAllCoupons())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{code}/activate")
    public ResponseEntity<ApiResponse<Object>> activateCoupon(@PathVariable String code) {

        service.activateCoupon(code);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Coupon activated")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{code}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateCoupon(@PathVariable String code) {

        service.deactivateCoupon(code);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Coupon deactivated")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

}