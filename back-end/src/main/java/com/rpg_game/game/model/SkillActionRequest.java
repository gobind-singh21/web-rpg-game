package com.rpg_game.game.model;

import java.util.HashMap;

public record SkillActionRequest(int currentCharacterId, String currentTeam, HashMap<Integer, CharacterSnapshot> currentLineup) {}
