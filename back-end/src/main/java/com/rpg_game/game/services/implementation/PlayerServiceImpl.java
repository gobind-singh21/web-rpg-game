package com.rpg_game.game.services.implementation;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.ChangePasswordRequest;
import com.rpg_game.game.model.SignupRequest;
import com.rpg_game.game.model.SignupResponse;
import com.rpg_game.game.repositories.PlayerRepository;
import com.rpg_game.game.security.details.PlayerDetailsService;
import com.rpg_game.game.repositories.CharacterRepository;
import org.springframework.security.core.Authentication;
import com.rpg_game.game.services.PlayerService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CharacterRepository characterRepository;
    private final JwtEncoder jwtEncoder;
    private final PlayerDetailsService playerDetailsService;

    @Value("${rpg_game.app.jwtExpirationMs}") 
    private long jwtLoginExpirationMs;

    public PlayerServiceImpl(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, CharacterRepository characterRepository, JwtEncoder jwtEncoder, PlayerDetailsService playerDetailsService) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.characterRepository = characterRepository;
        this.jwtEncoder = jwtEncoder;
        this.playerDetailsService = playerDetailsService;
    }

    @Override
    @Transactional
    public SignupResponse signupPlayer(SignupRequest signupRequest) {
        if (playerRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        if (playerRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        Player newPlayer = new Player();
        newPlayer.setUsername(signupRequest.getUsername());
        newPlayer.setEmail(signupRequest.getEmail());

        newPlayer.setPasswordDigest(passwordEncoder.encode(signupRequest.getPassword()));

        Player savedPlayer = playerRepository.save(newPlayer);

        // To assign free characters
        // assignFreeCharactersToPlayer(savedPlayer);

        UserDetails userDetails = playerDetailsService.loadUserByUsername(savedPlayer.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Instant now = Instant.now();
        long expiry = jwtLoginExpirationMs / 1000;

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String scope = userDetails.getAuthorities().stream() 
            .map(a -> a.getAuthority())
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(expiry, ChronoUnit.SECONDS))
            .subject(userDetails.getUsername()) 
            .claim("roles", scope)
            .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new SignupResponse(savedPlayer.getId(), savedPlayer.getUsername(), savedPlayer.getEmail(), token);
    }

    private void assignFreeCharactersToPlayer(Player player) {
        Set<Character> freeCharacterTemplates = characterRepository.findByCharacterCost(0.0);
        for (Character freeCharTemplate: freeCharacterTemplates) {
            player.addCharacter(freeCharTemplate);
        }
        playerRepository.save(player);
    }

    @Override
    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public Optional<Player> findByEmail(String email) {
        return playerRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return playerRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        Player player = playerRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Authenticated player not found. This should not happen."));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), player.getPasswordDigest())) {
            throw new BadCredentialsException("Current password does not match.");
        }

        player.setPasswordDigest(passwordEncoder.encode(request.getNewPassword()));
        
        playerRepository.save(player);
    }
}
