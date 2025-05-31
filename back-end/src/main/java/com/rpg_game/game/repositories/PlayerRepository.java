package com.rpg_game.game.repositories;

import org.springframework.data.repository.CrudRepository;

import com.rpg_game.game.entity.Player;

public interface PlayerRepository extends CrudRepository<Player, Integer> {
  
}
