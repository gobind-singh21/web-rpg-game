package com.rpg_game.game.controller;

import com.rpg_game.game.entity.Character;
import com.rpg_game.game.services.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/characters") 
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    /**
     * Get all characters belonging to the currently authenticated user.
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<Character>> getAllCharactersForCurrentUser() {
        Set<Character> characters = characterService.getAllCharactersForCurrentUser();
        return ResponseEntity.ok(characters);
    }

    /**
     * Get a single character by ID, ensuring it belongs to the currently authenticated user.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Character> getCharacterByIdForCurrentUser(@PathVariable Integer id) {
        Optional<Character> character = characterService.getCharacterByIdForCurrentUser(id);
        return character.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build()); 
    }

    @GetMapping("/for-sale")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<Character>> getCharactersForSale() {
        Set<Character> charactersForSale = characterService.getCharactersForSale();
        return ResponseEntity.ok(charactersForSale);
    }

    @GetMapping("/all")
    public ResponseEntity<Set<Character>> getAllCharacters() { 
        Set<Character> allCharacters = characterService.getAllCharacters();
        return ResponseEntity.ok(allCharacters);
    }
}