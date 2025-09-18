package com.carrent.carrental.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carrent.carrental.dto.UserDTO;
import com.carrent.carrental.entity.User;
import com.carrent.carrental.enums.UserRole;
import com.carrent.carrental.mappers.UserMapper;
import com.carrent.carrental.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toDTO(user);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setContactNo(userDTO.getContactNo());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setUserRole(userDTO.getUserRole() != null ? Enum.valueOf(UserRole.class, userDTO.getUserRole()) : null);

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
