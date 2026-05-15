package com.ecorides.mappers;

import com.ecorides.entity.User;
import com.ecorides.payload.request.UserUpdateRequest;
import com.ecorides.payload.response.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {


    public static UserResponse toUserResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .address(user.getAddress())
                .userRole(user.getUserRole())
                .build();
    }


    public static List<UserResponse> toUserResponseList(List<User> users) {
        if (users == null || users.isEmpty()) return List.of();

        return users.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }


    public static void updateUserFromRequest(User user, UserUpdateRequest request) {
        if (user == null || request == null) return;

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
    }
}