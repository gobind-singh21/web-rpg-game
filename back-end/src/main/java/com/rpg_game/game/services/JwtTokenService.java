package com.rpg_game.game.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.passwordResetExpirationMs}") 
    private long jwtPasswordResetExpirationMs;

    public JwtTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Generates a JWT specifically for password reset purposes.
     * Includes the user's username and the password_reset_version.
     * 
     * @param username The username for whom the token is generated.
     * @param resetVersion The current password_reset_version from the Player entity.
     * @return A signed JWT string.
     */
    public String generatePasswordResetToken(String username, Long resetVersion) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                .issuer("self")
                                .issuedAt(now)
                                .expiresAt(now.plusMillis(jwtPasswordResetExpirationMs))
                                .subject(username)
                                .claim("purpose", "password_reset")
                                .claim("resetVersion", resetVersion)
                                .build();
        
        JwsHeader header = JwsHeader.with(() -> "HS256").build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /**
     * Parses and validates a password reset JWT.
     * 
     * @param token token The JWT string to parse.
     * @return A Jwt object containing the token's claims.
     * @throws org.springframework.security.oauth2.jwt.JwtException if the token is invalid or expired.
     */
    public Jwt parsePasswordResetToken(String token) {
        return this.jwtDecoder.decode(token);
    }

    /**
     * Helper to get the configured password reset token expiration in milliseconds.
     * 
     * @return Expiration time in milliseconds.
     */
    public long getJwtPasswordResetExpirationMs() {
        return jwtPasswordResetExpirationMs;
    }
}
