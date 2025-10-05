package org.example.lab.config;

import lombok.RequiredArgsConstructor;
import org.example.lab.entity.User;
import org.example.lab.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@example.com")
                        .username("admin")
                        .password(passwordEncoder.encode("Admin123!"))
                        .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                        .build();
                userRepository.save(admin);
                System.out.println("Admin user created: admin@example.com / Admin123!");
            }
        };
    }
}