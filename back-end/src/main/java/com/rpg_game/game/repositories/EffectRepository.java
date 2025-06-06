package com.rpg_game.game.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

// import org.springframework.data.repository.CrudRepository;

import com.rpg_game.game.entity.Effect;

// public interface EffectRepository extends CrudRepository<Effect, Integer> {}

public interface EffectRepository extends JpaRepository<Effect, Long> { // Or whatever your ID type is
    Effect findByName(String name);
  }
