package com.ecorides.service.Impl;


import com.ecorides.entity.User;
import com.ecorides.exception.UserException;
import com.ecorides.mappers.UserMapper;
import com.ecorides.payload.request.PasswordUpdateRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.UserResponse;
import com.ecorides.repository.UserRepository;
import com.ecorides.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<UserResponse> getAllUsers() {
        return UserMapper.toUserResponseList(userRepository.findAll());
    }


    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = findUserById(id);


        if (request.getEmail() != null &&
                userRepository.existsByEmail(request.getEmail()) &&
                !request.getEmail().equals(user.getEmail())) {

            throw new UserException("Email already in use", 409);
        }


        UserMapper.updateUserFromRequest(user, request);

        userRepository.save(user);

        return UserMapper.toUserResponse(user);
    }


    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
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
            throw new UserException("Old password is incorrect", 401);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserException("Passwords do not match", 400);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with id: " + id, 404));
    }
}