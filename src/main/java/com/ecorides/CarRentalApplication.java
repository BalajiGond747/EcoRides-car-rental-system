package com.ecorides;

import com.ecorides.domain.AuthProvider;
import com.ecorides.domain.UserRole;
import com.ecorides.entity.User;
import com.ecorides.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class CarRentalApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {

        SpringApplication.run(CarRentalApplication.class, args);

    }

    @Bean
    CommandLineRunner initAdmin(

            @Value("${app.admin.email}") String adminEmail, @Value("${app.admin.password}") String adminPassword, @Value("${app.admin.first-name}") String firstName, @Value("${app.admin.last-name}") String lastName, @Value("${app.admin.phone}") String phone, @Value("${app.admin.address}") String address) {

        return args -> {

            if (!userRepository.existsByEmail(adminEmail)) {

                User admin = new User();

                admin.setFirstName(firstName);
                admin.setLastName(lastName);
                admin.setEmail(adminEmail);
                admin.setPhone(phone);
                admin.setAddress(address);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setUserRole(UserRole.ADMIN);
                admin.setIsActive(true);
                admin.setIsVerified(true);
                admin.setProvider(AuthProvider.LOCAL);

                userRepository.save(admin);

            }
        };
    }

}