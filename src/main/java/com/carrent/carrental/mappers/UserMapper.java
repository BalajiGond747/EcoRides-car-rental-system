package com.carrent.carrental.mappers;

import com.carrent.carrental.dto.UserDTO;
import com.carrent.carrental.entity.User;
import com.carrent.carrental.enums.UserRole;

public class UserMapper {

    // Convert User entity to UserDTO
    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getContactNo(),
            user.getAddress(),
            user.getUserRole().name() // convert enum to String if DTO has String role
        );
    }

    // Convert UserDTO to User entity
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        // user.setPassword(...) // skip if DTO does not have password
        user.setContactNo(dto.getContactNo());
        user.setAddress(dto.getAddress());
        user.setUserRole(UserRole.valueOf(dto.getUserRole())); // convert String back to enum

        return user;
    }
}
