package com.ecorides.service;

import com.ecorides.payload.request.AdminCreateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.UserResponse;

import java.util.List;

public interface AdminService {

    List<UserResponse> getAllAdmins();

    UserResponse getAdminById(Long id);

    UserResponse createAdmin(AdminCreateRequest request);

    UserResponse updateAdmin(Long id, UserUpdateRequest request);

    void activateAdmin(Long id);

    void deactivateAdmin(Long id);

    void deleteAdmin(Long id);
}