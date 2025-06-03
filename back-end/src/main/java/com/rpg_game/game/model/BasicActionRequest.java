package com.rpg_game.game.model;

import java.util.List;

import com.rpg_game.game.entity.Character;

public record BasicActionRequest(int currentCharacterIndex, int targetIndex, List<Character> allies, List<Character> enemies, List<Character> currentLineup) {}
