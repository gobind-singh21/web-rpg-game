package com.rpg_game.game.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rpg_game.game.model.SignupRequest;
import com.rpg_game.game.services.PlayerService;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(PlayerService playerService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!playerService.existsByUsername("testuser")) {
                SignupRequest signupRequest = new SignupRequest();
                signupRequest.setUsername("testuser");
                signupRequest.setEmail("test@example.com");
                signupRequest.setPassword("password123"); 
                try {
                    playerService.signupPlayer(signupRequest);
                    System.out.println("Created test user: testuser");
                } catch (IllegalArgumentException e) {
                    System.err.println("Failed to create test user: " + e.getMessage());
                }
            }

            if (!playerService.existsByUsername("adminuser")) {
                SignupRequest adminSignupRequest = new SignupRequest();
                adminSignupRequest.setUsername("adminuser");
                adminSignupRequest.setEmail("admin@example.com");
                adminSignupRequest.setPassword("adminpassword");

                try {
                    playerService.signupPlayer(adminSignupRequest);
                    System.out.println("Created admin user: adminuser");
                } catch (IllegalArgumentException e) {
                    System.err.println("Failed to create admin user: " + e.getMessage());
                }
            }
        };
    }
}