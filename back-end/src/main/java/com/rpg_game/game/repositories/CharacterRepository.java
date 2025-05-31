package com.rpg_game.game.repositories;

import org.springframework.data.repository.CrudRepository;
import com.rpg_game.game.entity.Character;

public interface CharacterRepository extends CrudRepository<Character, Integer> {}
