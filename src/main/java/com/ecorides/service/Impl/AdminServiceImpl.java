package com.ecorides.service.Impl;

import com.ecorides.domain.AuthProvider;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.User;
import com.ecorides.exception.BadRequestException;
import com.ecorides.exception.ConflictException;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.exception.UserException;
import com.ecorides.mappers.UserMapper;
import com.ecorides.payload.request.AdminCreateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.payload.response.UserResponse;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllAdmins(int page, int size, String sortBy, String sortDir) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Set<String> allowedSortFields = Set.of("id", "firstName", "lastName", "email", "createdAt", "updatedAt");

        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<User> adminPage = userRepository.findByUserRole(UserRole.ADMIN, pageable);

        List<UserResponse> admins = UserMapper.toUserResponseList(adminPage.getContent());

        return PageResponse.<UserResponse>builder()
                .content(admins)
                .page(adminPage.getNumber())
                .size(adminPage.getSize())
                .totalElements(adminPage.getTotalElements())
                .totalPages(adminPage.getTotalPages())
                .first(adminPage.isFirst())
                .last(adminPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getAdminById(Long id) {

        User admin = findAdminById(id);

        return UserMapper.toUserResponse(admin);
    }

    @Override
    public UserResponse createAdmin(AdminCreateRequest request) {

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new UserException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number already exists");
        }

        User admin = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .userRole(UserRole.ADMIN)
                .isActive(true)
                .isVerified(true)
                .provider(AuthProvider.LOCAL)
                .build();

        User saved = userRepository.save(admin);

        return UserMapper.toUserResponse(saved);
    }

    @Override
    public UserResponse updateAdmin(Long id, UserUpdateRequest request) {

        User admin = findAdminById(id);
        UserMapper.updateUserFromRequest(admin, request);
        return UserMapper.toUserResponse(userRepository.save(admin));
    }

    @Override
    public void activateAdmin(Long id) {

        User admin = findAdminById(id);
        admin.setIsActive(true);
        userRepository.save(admin);
    }

    @Override
    public void deactivateAdmin(Long id) {

        User admin = findAdminById(id);
        ensureAnotherActiveAdminExists(id);
        admin.setIsActive(false);
        userRepository.save(admin);
    }

    @Override
    public void deleteAdmin(Long id) {

        User admin = findAdminById(id);
        ensureAnotherActiveAdminExists(id);
        admin.setIsActive(false);

        userRepository.save(admin);
    }

    private User findAdminById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));

        if (user.getUserRole() != UserRole.ADMIN) {
            throw new ResourceNotFoundException("Admin not found with id: " + id);
        }

        return user;
    }

    private void ensureAnotherActiveAdminExists(Long currentAdminId) {

        long activeAdmins = userRepository.countByUserRoleAndIsActiveTrue(UserRole.ADMIN);

        User currentAdmin = userRepository.findById(currentAdminId)
                .orElse(null);

        if (currentAdmin != null && currentAdmin.getIsActive() && activeAdmins <= 1) {
            throw new BadRequestException("Cannot deactivate or delete the last active admin");
        }
    }
}