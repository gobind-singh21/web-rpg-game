package com.rpg_game.game.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.payload.AuthResponse;
import com.rpg_game.game.payload.LoginRequest;
import com.rpg_game.game.payload.SignupRequest;
import com.rpg_game.game.service.PlayerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final PlayerService playerService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthController(PlayerService playerService, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.playerService = playerService;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Handles new player registrations.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            Player registeredPlayer = playerService.signupPlayer(signupRequest);
            return new ResponseEntity<>("User registered successfully!!", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error during registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handles player login and issues a JWT upon successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(), 
                    loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Instant now = Instant.now();
            long expiry = 3600L; // token expiry in one hour

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
            
            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            Integer playerId = playerService.findByUsername(authentication.getName())
                                                .map(Player::getId)
                                                .orElse(null);

            return ResponseEntity.ok(new AuthResponse(authentication.getName(), token, playerId));
                                            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new AuthResponse("Login Failed", "Invalid Credentials", null), HttpStatus.UNAUTHORIZED);
        }
    }
}
