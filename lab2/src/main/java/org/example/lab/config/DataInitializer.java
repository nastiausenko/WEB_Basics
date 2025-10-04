package org.example.lab.config;

import lombok.RequiredArgsConstructor;
import org.example.lab.entity.User;
import org.example.lab.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN") // роль адміна
                        .build();
                userRepository.save(admin);
                System.out.println("Admin user created: admin@example.com / admin123");
            }
        };
    }
}