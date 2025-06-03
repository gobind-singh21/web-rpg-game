package com.rpg_game.game.service.implementation;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.payload.SignupRequest;
import com.rpg_game.game.repositories.PlayerRepository;
import com.rpg_game.game.service.PlayerService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {
    
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerServiceImpl(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Player signupPlayer(SignupRequest signupRequest) {
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

        return playerRepository.save(newPlayer);
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
}
