package com.rpg_game.game.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import com.rpg_game.game.entity.Player;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.repositories.CharacterRepository;
import com.rpg_game.game.repositories.PlayerRepository;

import jakarta.transaction.Transactional;

@Service
public class CharacterService { 
    
    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) { super(message); }
    }

    private Player getCurrentAuthenticatedPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return playerRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Authenticated player not found " + username));
    }

    public List<Character> getAllCharactersForCurrentUser() {
        Player currentPlayer = getCurrentAuthenticatedPlayer();
        return characterRepository.findByPlayersContaining(currentPlayer);
    }

    public Optional<Character> getCharacterByIdForCurrentUser(Integer characterId) {
        Player currentPlayer = getCurrentAuthenticatedPlayer();
        return characterRepository.findByIdAndPlayersContaining(characterId, currentPlayer);
    }
    
    /**
     * 
     * @param characterId
     * @return
     */
    @Transactional
    public Character assignExistingCharacterToCurrentUser(Integer characterId) {
        Player currentPlayer = getCurrentAuthenticatedPlayer();
        Character characterTemplate = characterRepository.findById(characterId)
                                            .orElseThrow(() -> new ResourceNotFoundException("Character template not found with ID: " + characterId));
        currentPlayer.addCharacter(characterTemplate);
        playerRepository.save(currentPlayer);

        return characterTemplate;
    }

    /**
     * 
     * @param newCharacter
     * @return
     */
    @Transactional
    public Character createAndAssignNewUniqueCharacter(Character newCharacter) {
        Player currentPlayer = getCurrentAuthenticatedPlayer();
        Character savedCharacter = characterRepository.save(newCharacter);

        currentPlayer.addCharacter(savedCharacter);

        playerRepository.save(currentPlayer);

        return savedCharacter;
    }

    public List<Character> getCharactersForSale() {
        Player currentPlayer = getCurrentAuthenticatedPlayer();
        List<Character> allPurchasableCharacters = characterRepository.findByCharacterCostGreaterThan(0.0);
        Set<Character> ownedCharacters = currentPlayer.getCharacters();
        List<Character> charactersAvailableToBuy = allPurchasableCharacters.stream()
            .filter(character -> !ownedCharacters.contains(character))
            .collect(Collectors.toList());
        return charactersAvailableToBuy;
    }
}
