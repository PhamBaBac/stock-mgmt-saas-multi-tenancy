package com.bacpham.saas.config;

import com.bacpham.saas.entities.User;
import com.bacpham.saas.entities.UserRole;
import com.bacpham.saas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByRole(UserRole.ROLE_PLATFORM_ADMIN).isEmpty()) {
            User admin = User.builder()
                    .username("superadmin")
                    .email("admin@saas.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .firstName("Platform")
                    .lastName("Admin")
                    .role(UserRole.ROLE_PLATFORM_ADMIN)
                    .tenant(null)
                    .enabled(true)
                    .createdBy("SYSTEM")
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println(">>> Platform Admin account created: superadmin / Admin@123");
        }
    }
}