package com.rpg_game.game.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.rpg_game.game.model.AuthResponse;
import com.rpg_game.game.model.ChangePasswordRequest;
import com.rpg_game.game.model.ForgotPasswordRequest;
import com.rpg_game.game.model.ResetPasswordRequest;
import com.rpg_game.game.model.LoginRequest;
import com.rpg_game.game.model.SignupRequest;
import com.rpg_game.game.model.SignupResponse;
import com.rpg_game.game.services.AuthService;
import com.rpg_game.game.services.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final PlayerService playerService;
    private final AuthService authService;

    public AuthController(PlayerService playerService, AuthService authService) {
        this.playerService = playerService;
        this.authService = authService;
    }

    /**
     * Handles new player registrations, automatically authenticates the user,
     * and returns a JWT.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            SignupResponse signupResponse = playerService.signupPlayer(signupRequest);
            return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.error("Error during registration for user {}: {}", signupRequest.getUsername(), e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error during registration: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles player login and issues a JWT upon successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.authenticatePlayer(loginRequest);
            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) { 
            return new ResponseEntity<>(new AuthResponse("Login Failed", e.getMessage(), null), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgot-password/request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.initiatePasswordReset(forgotPasswordRequest.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("message", "If an account with that email exists, a password reset link has been sent.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error processing password reset request for email {}: {}", forgotPasswordRequest.getEmail(), e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error processing password reset request. Please try again later.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint to reset the player's password using a 6-digit code.
     */
    @PostMapping("/reset-password-with-code")
    public ResponseEntity<Map<String, String>> resetPasswordWithCode(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authService.resetPasswordWithCode(resetPasswordRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password has been successfully reset!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Password reset failed for email {}: {}", resetPasswordRequest.getEmail(), e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage()); 
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error resetting password for email {}: {}", resetPasswordRequest.getEmail(), e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred during password reset. Please try again later.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            playerService.changePassword(username, changePasswordRequest);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error changing password for user: {}", SecurityContextHolder.getContext().getAuthentication().getName(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred while changing password: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles logout requests. 
     * For JWT-based authentication, this primarily informs the client to discard the token. 
     * It also explicitly clears the SecurityContextHolder for the current request.
     * This endpoint should be protected, requiring a valid JWT.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "unauthenticated user";
        logger.info("Logout endpoint hit by user: {}. Instructing client to discard JWT.", username);

        SecurityContextHolder.clearContext();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout request processed. Please discard your JWT on the client side.");
        return ResponseEntity.ok(response);
    }
}
