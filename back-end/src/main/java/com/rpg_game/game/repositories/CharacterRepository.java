package com.rpg_game.game.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.entity.Player;

public interface CharacterRepository extends CrudRepository<Character, Integer> {
    // Finding all characters belonging to the current player.
    List<Character> findByPlayersContaining(Player player);

    // Find a character and ensure that it belongs to that player.
    Optional<Character> findByIdAndPlayersContaining(Integer id, Player player);

    // To get the basic characters.
    List<Character> findByCharacterCost(Double characterCost);

    // To get the characters that are for sale.
    List<Character> findByCharacterCostGreaterThan(Double cost);
}
