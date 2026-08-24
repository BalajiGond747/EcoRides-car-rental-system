package com.ecorides.service;

import com.ecorides.payload.request.AdminCreateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.payload.response.UserResponse;

public interface AdminService {

    PageResponse<UserResponse> getAllAdmins(int page, int size, String sortBy, String sortDir);

    UserResponse getAdminById(Long id);

    UserResponse createAdmin(AdminCreateRequest request);

    UserResponse updateAdmin(Long id, UserUpdateRequest request);

    void activateAdmin(Long id);

    void deactivateAdmin(Long id);

    void deleteAdmin(Long id);
}