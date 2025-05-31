package com.rpg_game.game.repositories;

import org.springframework.data.repository.CrudRepository;

import com.rpg_game.game.entity.Ability;

public interface AbilityRepository extends CrudRepository<Ability, Integer> {
  
}
