package com.rpg_game.game.security.details;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.repositories.PlayerRepository;

@Service
public class PlayerDetailsService implements UserDetailsService {
    
    private final PlayerRepository playerRepository;

    public PlayerDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByUsername(username)
            .orElseGet(() -> playerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Player not found with username or email: " + username)));
        
        return new org.springframework.security.core.userdetails.User(
            player.getUsername(),
            player.getPasswordDigest(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
