package com.ecorides.service;

import com.ecorides.payload.request.PasswordUpdateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.payload.response.UserResponse;

public interface UserService {

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> getAllUsers(int page, int size, String search, Boolean isActive, String sortBy, String sortDir);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    void changePassword(Long userId, PasswordUpdateRequest request);
}