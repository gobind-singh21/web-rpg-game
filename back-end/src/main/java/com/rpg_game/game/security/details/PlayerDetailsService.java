package com.rpg_game.game.security.details;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.services.PlayerService;

@Service
public class PlayerDetailsService implements UserDetailsService {
    
    private final PlayerService playerService;

    public PlayerDetailsService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerService.findByUsername(username)
            .orElseGet(() -> playerService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Player not found with username or email: " + username)));
        
        return new org.springframework.security.core.userdetails.User(
            player.getUsername(),
            player.getPasswordDigest(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
