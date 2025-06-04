package com.rpg_game.game.model;

import java.util.HashMap;

public record BasicActionRequest(int currentCharacterId, int targetId, HashMap<Integer, CharacterSnapshot> currentLineup) {}
