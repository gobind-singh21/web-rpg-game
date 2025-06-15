package com.rpg_game.game.model;

import java.util.List;

public record BasicActionRequest(int currentCharacterId, int targetId, List<CharacterSnapshot> currentLineup) {}
