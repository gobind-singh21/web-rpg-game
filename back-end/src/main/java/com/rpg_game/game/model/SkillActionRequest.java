package com.rpg_game.game.model;

import java.util.List;

public record SkillActionRequest(int currentCharacterId, String currentTeam, List<CharacterSnapshot> currentLineup) {}
