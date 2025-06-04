package com.rpg_game.game.model;

import java.util.List;

import com.rpg_game.game.entity.Character;

public record SkillActionServiceResponse(boolean validMove, String message, List<Character> lineup) {}
