package com.rpg_game.game.services;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.model.LoginRequest;
import com.rpg_game.game.model.ResetPasswordRequest;

import jakarta.transaction.Transactional;

import com.rpg_game.game.model.AuthResponse; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit; 
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors; 

import org.springframework.security.oauth2.jose.jws.MacAlgorithm; 
import org.springframework.security.oauth2.jwt.JwsHeader; 
import org.springframework.security.oauth2.jwt.JwtClaimsSet; 
import org.springframework.security.oauth2.jwt.JwtEncoder; 
import org.springframework.security.oauth2.jwt.JwtEncoderParameters; 


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final PlayerService playerService; 
    private final PasswordEncoder encoder; 
    private final JwtTokenService jwtTokenService; 
    private final EmailService emailService; 
    private final JwtEncoder jwtEncoder; 

    // From application.properties
    // @Value("${rpg_game.app.frontendResetPasswordUrl}")
    // private String frontendResetPasswordUrl;

    @Value("${rpg_game.app.jwtExpirationMs}") 
    private long jwtLoginExpirationMs;

    @Value("${rpg_game.app.resetCodeExpirationMinutes}") 
    private long resetCodeExpirationMinutes;

    public AuthService(AuthenticationManager authenticationManager,
                       PlayerService playerService, 
                       PasswordEncoder encoder,
                       JwtTokenService jwtTokenService,
                       EmailService emailService,
                       JwtEncoder jwtEncoder) { 
        this.authenticationManager = authenticationManager;
        this.playerService = playerService;
        this.encoder = encoder;
        this.jwtTokenService = jwtTokenService;
        this.emailService = emailService;
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Authenticates a player for login and generates a JWT.
     * This logic is moved from your AuthController.
     * 
     * @param loginRequest Contains username/email and password.
     * @return AuthResponse with token and player details.
     */
    public AuthResponse authenticatePlayer(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Instant now = Instant.now();
            long expiry = jwtLoginExpirationMs / 1000; 

            JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build(); 

            String scope = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiry, ChronoUnit.SECONDS))
                .subject(authentication.getName())
                .claim("roles", scope)
                .build();

            String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

            // Use playerService to find the player ID
            Integer playerId = playerService.findByUsername(authentication.getName())
                                                .map(Player::getId)
                                                .orElse(null);

            return new AuthResponse(authentication.getName(), token, playerId);

        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getUsernameOrEmail(), e.getMessage());
            e.printStackTrace(); 
            throw new RuntimeException("Invalid Credentials", e); 
        }
    }

    /**
     * Initiates the password reset process by generating a JWT and sending a reset email.
     * 
     * @param email The email address of the player requesting the reset.
     * @throws RuntimeException if email sending fails or if other unexpected errors occur.
     */
    public void initiatePasswordReset(String email) {
        Optional<Player> playerOptional = playerService.findByEmail(email); 
        if (playerOptional.isEmpty()) {
            logger.warn("Password reset requested for non-existent email: {}", email);
            return; 
        }

        Player player = playerOptional.get();

        Random random = new Random();
        String resetCode = String.format("%06d", random.nextInt(1_000_000));

        LocalDateTime resetCodeExpiry = LocalDateTime.now().plusMinutes(resetCodeExpirationMinutes);

        Long newResetVersion = (player.getPasswordResetVersion() == null ? 1L : player.getPasswordResetVersion() + 1);

        player.setResetCode(resetCode);
        player.setResetCodeExpiry(resetCodeExpiry);
        player.setPasswordResetVersion(newResetVersion); 
        playerService.savePlayer(player);

        String subject = "Your Password Reset Code for Your Game Account";
        String body = "Dear " + player.getUsername() + ",<br><br>"
                    + "You have requested to reset your password. Your 6-digit password reset code is:<br><br>"
                    + "<h2 style=\"color: #007bff;\">" + resetCode + "</h2><br>" // Highlight the code
                    + "This code will expire in " + resetCodeExpirationMinutes + " minutes. Please use it on the password reset page.<br><br>"
                    + "If you did not request a password reset, please ignore this email.<br><br>"
                    + "Best regards,<br>"
                    + "Your Game Support Team";

        try {
            emailService.sendEmail(player.getEmail(), subject, body);
            logger.info("Password reset email successfully initiated for {}", player.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", player.getEmail(), e.getMessage());
            throw new RuntimeException("Could not send password reset email. Please try again or contact support.", e);
        }
    }

    @Transactional
    public void resetPasswordWithCode(ResetPasswordRequest request) {
        Optional<Player> playerOptional = playerService.findByEmail(request.getEmail());

        if (playerOptional.isEmpty()) {
            logger.warn("Password reset attempt for non-existent email: {}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or reset code.");
        }

        Player player = playerOptional.get();
        LocalDateTime now = LocalDateTime.now();

        if (player.getResetCode() == null || !player.getResetCode().equals(request.getResetCode())) {
            logger.warn("Invalid reset code provided for email {}. Expected: {}, Provided: {}",
                        request.getEmail(), player.getResetCode(), request.getResetCode());
            throw new IllegalArgumentException("Invalid email or reset code.");
        }

        if (player.getResetCodeExpiry() == null || now.isAfter(player.getResetCodeExpiry())) {
            player.setResetCode(null);
            player.setResetCodeExpiry(null);
            playerService.savePlayer(player); 
            logger.warn("Expired reset code provided for email {}", request.getEmail());
            throw new IllegalArgumentException("Reset code has expired. Please request a new one.");
        }

        player.setPasswordDigest(encoder.encode(request.getNewPassword()));

        player.setResetCode(null);
        player.setResetCodeExpiry(null);
        player.setPasswordResetVersion(player.getPasswordResetVersion() + 1);

        playerService.savePlayer(player);

        logger.info("Password successfully reset for user {}", player.getUsername());
    }
}