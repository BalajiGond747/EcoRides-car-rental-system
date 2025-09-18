package com.carrent.carrental.controller;

import com.carrent.carrental.dto.LoginRequestDTO;
import com.carrent.carrental.dto.LoginResponseDTO;
import com.carrent.carrental.dto.RegisterRequestDTO;
import com.carrent.carrental.dto.RegisterResponseDTO;
import com.carrent.carrental.service.AuthService;


import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO requestDTO) {
        RegisterResponseDTO responseDTO = authService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO responseDTO = authService.login(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
