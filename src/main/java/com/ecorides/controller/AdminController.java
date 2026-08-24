package com.ecorides.controller;

import com.ecorides.payload.request.AdminCreateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.ApiResponse;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.payload.response.UserResponse;
import com.ecorides.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllAdmins(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<UserResponse> admins = adminService.getAllAdmins(page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.<PageResponse<UserResponse>>builder()
                .success(true)
                .message("Admins fetched successfully")
                .data(admins)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getAdmin(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Admin fetched successfully")
                .data(adminService.getAdminById(id))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(@Valid @RequestBody AdminCreateRequest request) {

        UserResponse admin = adminService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Admin created successfully")
                        .data(admin)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateAdmin(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Admin updated successfully")
                .data(adminService.updateAdmin(id, request))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Object>> activateAdmin(@PathVariable Long id) {

        adminService.activateAdmin(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Admin activated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateAdmin(@PathVariable Long id) {

        adminService.deactivateAdmin(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Admin deactivated successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAdmin(@PathVariable Long id) {

        adminService.deleteAdmin(id);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Admin deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
}