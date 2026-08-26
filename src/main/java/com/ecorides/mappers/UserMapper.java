package com.ecorides.mappers;

import com.ecorides.entity.User;
import com.ecorides.payload.request.UserRegisterRequest;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toUserEntity(UserRegisterRequest userRegisterRequest) {

        return User.builder()
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .password(userRegisterRequest.getPassword())
                .phone(userRegisterRequest.getPhone())
                .address(userRegisterRequest.getAddress())
                .build();
    }

    public static UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .address(user.getAddress())
                .userRole(user.getUserRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static List<UserResponse> toUserResponseList(List<User> users) {

        return users.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public static void updateUserFromRequest(User user, UserUpdateRequest request) {

        if (request == null) {
            return;
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

    }
}