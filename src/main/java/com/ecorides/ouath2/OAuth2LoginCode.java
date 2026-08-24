package com.ecorides.ouath2;

import com.ecorides.domain.UserRole;

import java.time.Instant;

public record OAuth2LoginCode(Long userId, String email, UserRole userRole, Instant expiresAt) {
}