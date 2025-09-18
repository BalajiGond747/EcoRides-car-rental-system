package com.carrent.carrental.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carrent.carrental.dto.LoginRequestDTO;
import com.carrent.carrental.dto.LoginResponseDTO;
import com.carrent.carrental.dto.RegisterRequestDTO;
import com.carrent.carrental.dto.RegisterResponseDTO;
import com.carrent.carrental.entity.User;
import com.carrent.carrental.enums.UserRole; // Ensure UserRole enum is imported
import com.carrent.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // 🔹 Register a new user
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User newUser = new User();
        newUser.setFirstName(requestDTO.getFirstName());
        newUser.setLastName(requestDTO.getLastName());
        newUser.setEmail(requestDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        newUser.setContactNo(requestDTO.getContactNo());
        newUser.setAddress(requestDTO.getAddress());

        // Use enum constants correctly
        newUser.setUserRole(
                requestDTO.getUserRole() != null ? requestDTO.getUserRole() : UserRole.ROLE_USER
        );

        User savedUser = userRepository.save(newUser);

        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getContactNo(),
                savedUser.getAddress(),
                savedUser.getUserRole() // type matches UserRole enum
        );
    }

    // 🔹 Authenticate user & issue JWT
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + requestDTO.getEmail()
                ));

        String token = jwtService.generateToken(user);

        return new LoginResponseDTO(
                token,
                user.getUserRole().name(), // return role as string
                user.getEmail()
        );
    }
}
