package com.rpg_game.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.rpg_game.game.services.CharacterService;
import com.rpg_game.game.entity.Character;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/characters")
@PreAuthorize("isAuthenticated()")
public class CharacterController {
    
    @Autowired
    private CharacterService characterService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<List<Character>> getAllCharactersForCurrentUser() {
        List<Character> characters = characterService.getAllCharactersForCurrentUser();
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Character> getCharacterByIdForCurrentUser(@PathVariable Integer id) {
        Optional<Character> character = characterService.getCharacterByIdForCurrentUser(id);    
        return character.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()); // 404 not found error
    }

    @PostMapping("/assign-template/{characterTemplateId}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Character> assignCharacterTemplate(@PathVariable Integer characterTemplateId) {
        try {
            Character assignedChar = characterService.assignExistingCharacterToCurrentUser(characterTemplateId);
            return ResponseEntity.ok(assignedChar);
        } catch (RuntimeException e) { 
            return ResponseEntity.notFound().build(); 
        }
    }

    @GetMapping("/for-sale")
    @PreAuthorize("hasRole('USER')") // Only authenticated users can see characters for sale
    public ResponseEntity<List<Character>> getCharactersForSale() {
        List<Character> charactersForSale = characterService.getCharactersForSale();
        return ResponseEntity.ok(charactersForSale);
    }
}
