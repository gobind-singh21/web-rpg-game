package com.rpg_game.game.model;

import java.util.List;

import com.rpg_game.game.entity.Character;

public record SkillActionRequest(int currentCharacterIndex, List<Character> allies, List<Character> enemies, List<Character> currentLineup) {}
