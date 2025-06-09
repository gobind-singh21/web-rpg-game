package com.rpg_game.game.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import com.rpg_game.game.entity.Character;
import com.rpg_game.game.entity.Player;

public interface CharacterRepository extends CrudRepository<Character, Integer> {
    Set<Character> findByPlayersContaining(Player player);

    Optional<Character> findByIdAndPlayersContaining(Integer id, Player player);

    // To get the character all available for sale to the current user.
    Set<Character> findByCharacterCostGreaterThan(Double cost);

    Set<Character> findByCharacterCost(Double characterCost);

    Set<Character> findAll();
}
