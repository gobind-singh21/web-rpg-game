package com.rpg_game.game.services.implementation;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.model.SignupRequest;
import com.rpg_game.game.repositories.PlayerRepository;
import com.rpg_game.game.services.PlayerService;
import com.rpg_game.game.repositories.CharacterRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {
    
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CharacterRepository characterRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, CharacterRepository characterRepository) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.characterRepository = characterRepository;
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

        Player savedPlayer = playerRepository.save(newPlayer);

        assignFreeCharactersToPlayer(savedPlayer);

        return savedPlayer;
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

    private void assignFreeCharactersToPlayer(Player player) {
        List<Character> freeCharacterTemplates = characterRepository.findByCharacterCost(0.0);

        for (Character freeCharTemplate: freeCharacterTemplates) {
            player.addCharacter(freeCharTemplate);
        }

        playerRepository.save(player);
    }
}
