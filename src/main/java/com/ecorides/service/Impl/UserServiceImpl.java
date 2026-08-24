package com.ecorides.service.Impl;

import com.ecorides.domain.UserRole;
import com.ecorides.entity.User;
import com.ecorides.exception.ResourceNotFoundException;
import com.ecorides.exception.UserException;
import com.ecorides.mappers.UserMapper;
import com.ecorides.payload.request.PasswordUpdateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.PageResponse;
import com.ecorides.payload.response.UserResponse;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getUserById(Long id) {

        User user = findUserById(id);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size, String search, Boolean isActive, String sortBy, String sortDir) {

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

        Page<User> userPage;

        boolean hasSearch = search != null && !search.trim()
                .isEmpty();

        if (hasSearch && isActive != null) {

            userPage = userRepository.findByUserRoleAndIsActiveAndSearch(UserRole.USER, isActive, search.trim(), pageable);

        } else if (hasSearch) {

            userPage = userRepository.findByUserRoleAndSearch(UserRole.USER, search.trim(), pageable);

        } else if (isActive != null) {

            userPage = userRepository.findByUserRoleAndIsActive(UserRole.USER, isActive, pageable);

        } else {

            userPage = userRepository.findByUserRole(UserRole.USER, pageable);
        }

        List<UserResponse> content = UserMapper.toUserResponseList(userPage.getContent());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = findUserById(id);
        UserMapper.updateUserFromRequest(user, request);
        userRepository.save(user);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = findUserById(id);
        deactivateUser(id);
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(Long id) {

        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {

        User user = findUserById(id);
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, PasswordUpdateRequest request) {

        User user = findUserById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UserException("Old password is incorrect");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {
            throw new UserException("Passwords do not matching");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new UserException("New password cannot be same as old password");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

}