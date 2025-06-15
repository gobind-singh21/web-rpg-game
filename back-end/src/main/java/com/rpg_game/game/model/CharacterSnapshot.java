package com.rpg_game.game.model;

import java.util.List;

import com.rpg_game.game.entity.Effect;

public record CharacterSnapshot(int id, String team, int currentHealth, int shield, List<Effect> effects) {}
